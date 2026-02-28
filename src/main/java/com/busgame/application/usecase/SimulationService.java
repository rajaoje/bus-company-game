// application/usecase/SimulationService.java
package com.busgame.application.usecase;

import com.busgame.domain.event.ScheduleCompletedEvent;
import com.busgame.domain.event.ScheduleStartedEvent;
import com.busgame.domain.event.BusBreakdownEvent;
import com.busgame.domain.model.*;
import com.busgame.domain.port.in.SimulationUseCase;
import com.busgame.domain.port.out.BusRepository;
import com.busgame.domain.port.out.GameStateRepository;
import com.busgame.domain.port.out.MaintenanceRecordRepository;
import com.busgame.domain.port.out.ScheduleRepository;
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
 * A chaque tick(), il :
 * 1. Fait avancer l'horloge du jeu
 * 2. Demarre les horaires qui doivent commencer
 * 3. Termine les horaires qui doivent finir
 * 4. Tire au sort des evenements aleatoires
 * 5. Publie des evenements Spring pour que les handlers reagissent
 */
@Service
public class SimulationService implements SimulationUseCase {

    private static final Logger log = LoggerFactory.getLogger(SimulationService.class);
    private static final double BREAKDOWN_PROBABILITY = 0.00137; // 2% de chance par tick

    private final GameStateRepository gameStateRepository;
    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final BusRepository busRepository;

    private final Random random = new Random();

    public SimulationService(GameStateRepository gameStateRepository,
                             ScheduleRepository scheduleRepository,
                             ApplicationEventPublisher eventPublisher, MaintenanceRecordRepository maintenanceRecordRepository, BusRepository busRepository) {
        this.gameStateRepository = gameStateRepository;
        this.scheduleRepository = scheduleRepository;
        this.eventPublisher = eventPublisher;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.busRepository = busRepository;
    }

    @Override
    @Transactional
    public GameState startSimulation(LocalDateTime startTime) {
        // Si une partie existe deja, on la reprend — sinon on en cree une nouvelle
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

    /**
     * Le coeur du moteur.
     * Cette methode est appelee automatiquement par le Scheduler Spring,
     * mais peut aussi etre appelee manuellement depuis le controller.
     *
     * IMPORTANT : on ne met pas @Transactional ici — chaque sous-operation
     * gere sa propre transaction via les handlers d'evenements.
     * Ainsi, si un evenement echoue, les autres continuent.
     */
    @Override
    public GameState processTick() {
        GameState gameState = gameStateRepository.find().orElse(null);

        if (gameState == null || !gameState.isRunning()) {
            return gameState;
        }

        // Avancer l'horloge et sauvegarder dans sa propre transaction
        LocalDateTime newGameTime = advanceTime(gameState);

        log.info("Tick — Heure du jeu : {}", newGameTime);

        // Traiter chaque type d'evenement independamment
        processScheduleStarts(newGameTime);
        processScheduleCompletions(newGameTime);
        processRandomEvents(newGameTime);
        processMaintenanceExits(newGameTime);


        return gameStateRepository.find().orElseThrow();
    }

    @Transactional
    protected LocalDateTime advanceTime(GameState gameState) {
        LocalDateTime newTime = gameState.tick();
        gameStateRepository.save(gameState);
        return newTime;
    }

    /**
     * Cherche les horaires PLANNED dont l'heure de debut est atteinte
     * et publie un evenement de demarrage pour chacun.
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
                log.error("Erreur au demarrage de l'horaire {} : {}",
                        schedule.getId(), e.getMessage());
            }
        }
    }

    /**
     * Cherche les horaires IN_PROGRESS dont l'heure de fin est atteinte
     * et publie un evenement de completion pour chacun.
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
                        schedule.getRouteId(),
                        schedule.getDurationHours(),
                        gameTime
                ));
                log.info("Horaire termine : {}", schedule.getId());
            } catch (Exception e) {
                log.error("Erreur a la completion de l'horaire {} : {}",
                        schedule.getId(), e.getMessage());
            }
        }
    }

    /**
     * Tire au sort des pannes aleatoires sur les bus en service.
     * Chaque bus IN_SERVICE a une probabilite de tomber en panne a chaque tick.
     */
    private void processRandomEvents(LocalDateTime gameTime) {
        List<Schedule> activeSchedules = scheduleRepository
                .findByStatus(ScheduleStatus.IN_PROGRESS);

        for (Schedule schedule : activeSchedules) {
            if (random.nextDouble() < BREAKDOWN_PROBABILITY) {
                log.warn("Panne aleatoire sur le bus {} !", schedule.getBusId());
                try {
                    eventPublisher.publishEvent(new BusBreakdownEvent(
                            schedule.getBusId(),
                            schedule.getId(),
                            gameTime
                    ));
                } catch (Exception e) {
                    log.error("Erreur lors du traitement de la panne : {}", e.getMessage());
                }
            }
        }
    }
    // La methode mise a jour :
    private void processMaintenanceExits(LocalDateTime gameTime) {
        List<MaintenanceRecord> readyRecords =
                maintenanceRecordRepository.findRecordsReadyToComplete(gameTime);

        for (MaintenanceRecord record : readyRecords) {
            try {
                // Cloturer le dossier de maintenance
                record.complete(gameTime);
                maintenanceRecordRepository.save(record);

                // Remettre le bus disponible
                Bus bus = busRepository.findById(record.getBusId()).orElseThrow();
                bus.returnFromMaintenance();
                busRepository.save(bus);

                log.info("Bus {} sorti de maintenance apres {:.1f}h — Dossier {}",
                        record.getBusId(),
                        record.getActualDurationHours(),
                        record.getId());
            } catch (Exception e) {
                log.error("Erreur sortie maintenance dossier {} : {}",
                        record.getId(), e.getMessage());
            }
        }
    }
}