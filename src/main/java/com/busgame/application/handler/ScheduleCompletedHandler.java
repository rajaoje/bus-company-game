// application/handler/ScheduleCompletedHandler.java
package com.busgame.application.handler;

import com.busgame.application.scheduler.SimulationScheduler;
import com.busgame.domain.event.ScheduleCompletedEvent;
import com.busgame.domain.model.*;
import com.busgame.domain.port.out.*;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reagit a la completion d'un horaire.
 * Responsabilites :
 * - Passer l'horaire a COMPLETED
 * - Remettre le bus AVAILABLE et ajouter le kilometrage parcouru
 * - Remettre le conducteur AVAILABLE et crediter ses heures
 */
@Component
public class ScheduleCompletedHandler {

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;

    private static final Logger log = LoggerFactory.getLogger(ScheduleCompletedHandler.class);

    public ScheduleCompletedHandler(ScheduleRepository scheduleRepository,
                                    BusRepository busRepository,
                                    DriverRepository driverRepository,
                                    RouteRepository routeRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository = busRepository;
        this.driverRepository = driverRepository;
        this.routeRepository = routeRepository;
    }

    @EventListener
    @Transactional
    public void handle(ScheduleCompletedEvent event) {
        // Completer l'horaire
        Schedule schedule = scheduleRepository.findById(event.scheduleId()).orElseThrow();
        schedule.complete();
        scheduleRepository.save(schedule);

        // Recuperer le parcours pour calculer le kilometrage reel
        Route route = routeRepository.findById(event.routeId()).orElseThrow();

        double routeDistanceKm       = route.getTotalDistance().kilometers();
        double routeDurationMinutes  = route.getEstimatedDurationMinutes();
        double shiftDurationMinutes  = event.durationHours() * 60.0;

        /**
         * Nombre de fois que le bus a parcouru le trajet pendant le shift.
         * On utilise Math.max(1, ...) pour garantir qu'on ajoute au moins
         * la distance d'un aller, meme si le shift est tres court.
         *
         * Exemple : shift de 3h, parcours de 45min et 18km
         * → 3×60 / 45 = 4 passages → 4 × 18 = 72 km
         */
        int numberOfPassages = (int) Math.max(
                1,
                Math.floor(shiftDurationMinutes / routeDurationMinutes)
        );

        int totalKmDriven = (int) Math.round(
                numberOfPassages * routeDistanceKm
        );

        log.info("Shift termine — {} passages sur '{}' → {} km parcourus",
                numberOfPassages, route.getName(), totalKmDriven);

        // Remettre le bus disponible et ajouter le kilometrage
        Bus bus = busRepository.findById(event.busId()).orElseThrow();
        bus.endService();
        bus.addMileage(totalKmDriven);
        busRepository.save(bus);

        // Remettre le conducteur disponible et crediter ses heures
        Driver driver = driverRepository.findById(event.driverId()).orElseThrow();
        driver.endDuty(event.durationHours());
        driverRepository.save(driver);
    }
}