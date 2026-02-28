// application/handler/BusBreakdownHandler.java
package com.busgame.application.handler;

import com.busgame.domain.event.BusBreakdownEvent;
import com.busgame.domain.model.*;
import com.busgame.domain.port.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Reagit a une panne de bus.
 * Responsabilites :
 * - Annuler l'horaire en cours
 * - Envoyer le bus en maintenance
 * - Remettre le conducteur disponible (il n'est plus au volant)
 */
// application/handler/BusBreakdownHandler.java — version finale

@Component
public class BusBreakdownHandler {

    private static final Logger log = LoggerFactory.getLogger(BusBreakdownHandler.class);
    private static final int MIN_MAINTENANCE_HOURS = 1;
    private static final int MAX_MAINTENANCE_HOURS = 4;

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final GameStateRepository gameStateRepository;
    private final Random random = new Random();

    public BusBreakdownHandler(ScheduleRepository scheduleRepository,
                               BusRepository busRepository,
                               DriverRepository driverRepository,
                               MaintenanceRecordRepository maintenanceRecordRepository,
                               GameStateRepository gameStateRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.gameStateRepository = gameStateRepository;
    }

    @EventListener
    @Transactional
    public void handle(BusBreakdownEvent event) {
        GameState gameState = gameStateRepository.find().orElseThrow();
        LocalDateTime currentGameTime = gameState.getCurrentGameTime();

        // Duree aleatoire entre 1 et 4 heures dans le temps du jeu
        int maintenanceHours = MIN_MAINTENANCE_HOURS +
                random.nextInt(MAX_MAINTENANCE_HOURS - MIN_MAINTENANCE_HOURS + 1);
        LocalDateTime scheduledEndTime = currentGameTime.plusHours(maintenanceHours);

        // Annuler l'horaire en cours
        Schedule schedule = scheduleRepository
                .findById(event.affectedScheduleId()).orElseThrow();
        schedule.cancel();
        scheduleRepository.save(schedule);

        // Mettre le bus en maintenance
        Bus bus = busRepository.findById(event.busId()).orElseThrow();
        bus.endService();
        bus.sendToMaintenance();
        busRepository.save(bus);

        // Ouvrir le dossier de maintenance — c'est lui qui porte la duree
        MaintenanceRecord record = MaintenanceRecord.open(
                event.busId(),
                currentGameTime,
                scheduledEndTime,
                MaintenanceCause.BREAKDOWN
        );
        maintenanceRecordRepository.save(record);

        log.warn("Panne bus {} — Maintenance de {}h prevue jusqu'au {}",
                event.busId(), maintenanceHours, scheduledEndTime);

        // Liberer le conducteur
        Driver driver = driverRepository.findById(schedule.getDriverId()).orElseThrow();
        driver.endDuty(0);
        driverRepository.save(driver);
    }
}
