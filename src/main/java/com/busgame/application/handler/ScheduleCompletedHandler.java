// application/handler/ScheduleCompletedHandler.java
package com.busgame.application.handler;

import com.busgame.domain.event.ScheduleCompletedEvent;
import com.busgame.domain.model.*;
import com.busgame.domain.port.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Gestionnaire de l'evenement ScheduleCompleted.
 *
 * Responsabilites :
 * 1. Marquer le Schedule comme COMPLETED
 * 2. Calculer le kilometrage reel parcouru par le bus pendant le shift
 *    — le bus fait plusieurs allers sur le Trip pendant la duree du shift
 * 3. Remettre le Bus en statut AVAILABLE
 * 4. Crediter les heures au Driver et le remettre AVAILABLE
 *
 * Apres Niveau 2 GTFS : on utilise Trip pour calculer la duree
 * et la distance du parcours au lieu de Route directement.
 */
@Component
public class ScheduleCompletedHandler {

    private static final Logger log =
            LoggerFactory.getLogger(ScheduleCompletedHandler.class);

    private final ScheduleRepository  scheduleRepository;
    private final BusRepository        busRepository;
    private final DriverRepository     driverRepository;
    private final TripRepository       tripRepository;

    public ScheduleCompletedHandler(
            ScheduleRepository scheduleRepository,
            BusRepository busRepository,
            DriverRepository driverRepository,
            TripRepository tripRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository      = busRepository;
        this.driverRepository   = driverRepository;
        this.tripRepository     = tripRepository;
    }

    @EventListener
    @Transactional
    public void handle(ScheduleCompletedEvent event) {

        // ── 1. Completer le schedule ──────────────────────────────────
        Schedule schedule = scheduleRepository
                .findById(event.scheduleId())
                .orElseThrow(() -> new RuntimeException(
                        "Schedule introuvable : " + event.scheduleId()));

        schedule.complete();
        scheduleRepository.save(schedule);

        // ── 2. Calculer le kilometrage reel ───────────────────────────
        Trip trip = tripRepository
                .findById(event.tripId())
                .orElseThrow(() -> new RuntimeException(
                        "Trip introuvable : " + event.tripId()));

        double tripDistanceKm      = trip.getTotalDistance().kilometers();
        double tripDurationMinutes = trip.getEstimatedDurationMinutes();
        double shiftDurationMinutes = event.durationHours() * 60.0;

        /**
         * Nombre de fois que le bus a parcouru le trip pendant le shift.
         *
         * Exemple : shift de 4h, trip de 45min et 18km
         * → 4×60 / 45 = 5.33 → 5 passages → 5 × 18 = 90 km
         *
         * Math.max(1, ...) garantit qu'on ajoute au moins la distance
         * d'un aller, meme si le shift est tres court.
         *
         * On ne calcule le kilometrage que si le trip a une duree
         * estimee valide (au moins 2 arrets).
         */
        int totalKmDriven;

        if (tripDurationMinutes > 0 && tripDistanceKm > 0) {
            int numberOfPassages = (int) Math.max(
                    1,
                    Math.floor(shiftDurationMinutes / tripDurationMinutes)
            );
            totalKmDriven = (int) Math.round(
                    numberOfPassages * tripDistanceKm);

            log.info(
                    "Shift termine — trip '{}', {} passages, {} km parcourus",
                    trip.getHeadsign(),
                    numberOfPassages,
                    totalKmDriven
            );
        } else {
            // Trip sans stopTimes definis — kilometrage minimal
            totalKmDriven = 0;
            log.warn(
                    "Trip {} sans stopTimes definis — kilometrage non calcule",
                    event.tripId()
            );
        }

        // ── 3. Remettre le bus disponible avec le bon kilometrage ─────
        Bus bus = busRepository
                .findById(event.busId())
                .orElseThrow(() -> new RuntimeException(
                        "Bus introuvable : " + event.busId()));

        bus.endService();
        bus.addMileage(totalKmDriven);
        busRepository.save(bus);

        log.info(
                "Bus {} remis AVAILABLE — kilometrage total : {} km",
                bus.getBusNumber(),
                bus.getMileage()
        );

        // ── 4. Crediter les heures du conducteur ──────────────────────
        Driver driver = driverRepository
                .findById(event.driverId())
                .orElseThrow(() -> new RuntimeException(
                        "Conducteur introuvable : " + event.driverId()));

        driver.endDuty(event.durationHours());
        driverRepository.save(driver);

        log.info(
                "Conducteur {} remis AVAILABLE — {}/{} heures cette semaine",
                driver.getFirstName() + " " + driver.getLastName(),
                driver.getWeeklyHoursWorked(),
                driver.getMaxWeeklyHours()
        );
    }
}