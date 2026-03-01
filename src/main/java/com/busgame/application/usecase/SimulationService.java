// application/usecase/SimulationService.java
package com.busgame.application.usecase;

import com.busgame.domain.event.BusBreakdownEvent;
import com.busgame.domain.event.ScheduleCompletedEvent;
import com.busgame.domain.event.ScheduleStartedEvent;
import com.busgame.domain.model.*;
import com.busgame.domain.port.in.SimulationUseCase;
import com.busgame.domain.port.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Le cerveau de la simulation.
 *
 * A chaque tick() :
 * 1. Avance l'horloge du jeu
 * 2. Demarre les horaires dont l'heure de debut est atteinte
 * 3. Termine les horaires dont l'heure de fin est atteinte
 * 4. Sort de maintenance les bus dont la duree est ecoulee
 * 5. Tire au sort des pannes aleatoires
 *
 * Apres Niveau 2 GTFS : ScheduleCompletedEvent transporte
 * un TripId au lieu d'un RouteId — le handler utilise Trip
 * pour calculer la distance et le kilometrage.
 *
 * Chaque sous-operation a sa propre transaction independante —
 * si un evenement echoue, les autres continuent.
 */
@Service
public class SimulationService implements SimulationUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(SimulationService.class);

    private static final double BREAKDOWN_PROBABILITY = 0.02;

    private final GameStateRepository         gameStateRepository;
    private final ScheduleRepository          scheduleRepository;
    private final BusRepository               busRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final ApplicationEventPublisher   eventPublisher;
    private final Random random = new Random();

    public SimulationService(
            GameStateRepository gameStateRepository,
            ScheduleRepository scheduleRepository,
            BusRepository busRepository,
            MaintenanceRecordRepository maintenanceRecordRepository,
            ApplicationEventPublisher eventPublisher) {
        this.gameStateRepository         = gameStateRepository;
        this.scheduleRepository          = scheduleRepository;
        this.busRepository               = busRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.eventPublisher              = eventPublisher;
    }

    // ── Controles de la simulation ────────────────────────────────────

    @Override
    @Transactional
    public GameState startSimulation(LocalDateTime startTime) {
        GameState gameState = gameStateRepository.find()
                .orElse(GameState.initialize(startTime));
        gameState.start();
        return gameStateRepository.save(gameState);
    }

    @Override
    @Transactional(readOnly = true)
    public GameState getGameState() {
        return gameStateRepository.find()
                .orElseThrow(() -> new IllegalStateException(
                        "Aucune simulation n'a ete demarree."));
    }

    @Override
    @Transactional
    public GameState pauseSimulation() {
        GameState gameState = getGameState();
        gameState.pause();
        return gameStateRepository.save(gameState);
    }

    @Override
    @Transactional
    public GameState resumeSimulation() {
        GameState gameState = getGameState();
        gameState.resume();
        return gameStateRepository.save(gameState);
    }

    @Override
    @Transactional
    public GameState setSpeed(int multiplier) {
        GameState gameState = getGameState();
        gameState.setSpeedMultiplier(multiplier);
        return gameStateRepository.save(gameState);
    }

    // ── Coeur du moteur ───────────────────────────────────────────────

    /**
     * Pas de @Transactional global ici —
     * chaque sous-operation gere sa propre transaction.
     * Ainsi, si un evenement echoue, les autres continuent.
     */
    @Override
    public GameState processTick() {
        GameState gameState = gameStateRepository.find().orElse(null);

        if (gameState == null || !gameState.isRunning()) {
            return gameState;
        }

        // Avancer l'horloge dans sa propre transaction
        LocalDateTime newGameTime = advanceTime(gameState);

        log.info("Tick — Heure du jeu : {}", newGameTime);

        // Traiter chaque categorie d'evenements independamment
        processScheduleStarts(newGameTime);
        processScheduleCompletions(newGameTime);
        processMaintenanceExits(newGameTime);
        processRandomBreakdowns(newGameTime);

        return gameStateRepository.find().orElseThrow();
    }

    @Transactional
    protected LocalDateTime advanceTime(GameState gameState) {
        LocalDateTime newTime = gameState.tick();
        gameStateRepository.save(gameState);
        return newTime;
    }

    // ── Traitements par categorie ─────────────────────────────────────

    /**
     * Demarre les horaires PLANNED dont l'heure de debut est atteinte.
     * Publie ScheduleStartedEvent — le handler met a jour Bus et Driver.
     */
    private void processScheduleStarts(LocalDateTime gameTime) {
        List<Schedule> toStart = scheduleRepository
                .findSchedulesToStart(gameTime);

        for (Schedule schedule : toStart) {
            try {
                eventPublisher.publishEvent(new ScheduleStartedEvent(
                        schedule.getId(),
                        schedule.getBusId(),
                        schedule.getDriverId(),
                        gameTime
                ));
                log.info("Horaire demarre : {}", schedule.getId());
            } catch (Exception e) {
                log.error("Erreur demarrage horaire {} : {}",
                        schedule.getId(), e.getMessage());
            }
        }
    }

    /**
     * Termine les horaires IN_PROGRESS dont l'heure de fin est atteinte.
     *
     * Apres Niveau 2 GTFS : on passe tripId au lieu de routeId
     * dans l'evenement — le ScheduleCompletedHandler utilise Trip
     * pour calculer le nombre de passages et le kilometrage reel.
     */
    private void processScheduleCompletions(LocalDateTime gameTime) {
        List<Schedule> toComplete = scheduleRepository
                .findSchedulesToComplete(gameTime);

        for (Schedule schedule : toComplete) {
            try {
                eventPublisher.publishEvent(new ScheduleCompletedEvent(
                        schedule.getId(),
                        schedule.getBusId(),
                        schedule.getDriverId(),
                        schedule.getTripId(),        // TripId — Niveau 2 GTFS
                        schedule.getDurationHours(),
                        gameTime
                ));
                log.info("Horaire termine : {}", schedule.getId());
            } catch (Exception e) {
                log.error("Erreur completion horaire {} : {}",
                        schedule.getId(), e.getMessage());
            }
        }
    }

    /**
     * Sort de maintenance les bus dont le dossier MaintenanceRecord
     * a atteint sa scheduledEndTime.
     *
     * C'est MaintenanceRecord qui porte la duree de maintenance —
     * pas Bus directement (approche 2 choisie dans le projet).
     */
    private void processMaintenanceExits(LocalDateTime gameTime) {
        List<MaintenanceRecord> readyRecords =
                maintenanceRecordRepository
                        .findRecordsReadyToComplete(gameTime);

        for (MaintenanceRecord record : readyRecords) {
            try {
                // Cloturer le dossier
                record.complete(gameTime);
                maintenanceRecordRepository.save(record);

                // Remettre le bus disponible
                Bus bus = busRepository
                        .findById(record.getBusId())
                        .orElseThrow();
                bus.returnFromMaintenance();
                busRepository.save(bus);

                log.info(
                        "Bus {} sorti de maintenance apres {:.1f}h",
                        record.getBusId(),
                        record.getActualDurationHours()
                );
            } catch (Exception e) {
                log.error(
                        "Erreur sortie maintenance dossier {} : {}",
                        record.getId(), e.getMessage()
                );
            }
        }
    }

    /**
     * Tire au sort des pannes aleatoires sur les bus IN_PROGRESS.
     * Chaque bus en service a BREAKDOWN_PROBABILITY de chance
     * de tomber en panne a chaque tick.
     */
    private void processRandomBreakdowns(LocalDateTime gameTime) {
        List<Schedule> activeSchedules = scheduleRepository
                .findByStatus(ScheduleStatus.IN_PROGRESS);

        for (Schedule schedule : activeSchedules) {
            if (random.nextDouble() < BREAKDOWN_PROBABILITY) {
                log.warn("Panne aleatoire — bus {} !",
                        schedule.getBusId());
                try {
                    eventPublisher.publishEvent(new BusBreakdownEvent(
                            schedule.getBusId(),
                            schedule.getId(),
                            gameTime
                    ));
                } catch (Exception e) {
                    log.error("Erreur traitement panne bus {} : {}",
                            schedule.getBusId(), e.getMessage());
                }
            }
        }
    }
}