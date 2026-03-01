// application/usecase/ScheduleGenerationService.java
package com.busgame.application.usecase;

import com.busgame.domain.model.*;
import com.busgame.domain.model.Calendar;
import com.busgame.domain.port.in.ScheduleGenerationUseCase;
import com.busgame.domain.port.out.*;
import com.busgame.domain.service.CalendarResolutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

/**
 * Genere automatiquement les Schedules pour une journee donnee.
 *
 * Logique :
 * 1. Recuperer tous les Trips
 * 2. Pour chaque Trip, verifier si son service est actif ce jour
 * 3. Si oui, trouver un bus et un conducteur disponibles
 * 4. Creer le Schedule
 *
 * Si aucun bus ou conducteur n'est disponible pour un Trip,
 * on le signale dans le rapport sans echouer.
 */
@Service
@Transactional
public class ScheduleGenerationService
        implements ScheduleGenerationUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ScheduleGenerationService.class);

    private final TripRepository         tripRepository;
    private final CalendarRepository     calendarRepository;
    private final CalendarDateRepository calendarDateRepository;
    private final BusRepository          busRepository;
    private final DriverRepository       driverRepository;
    private final ScheduleRepository     scheduleRepository;

    private final CalendarResolutionService calendarResolver =
            new CalendarResolutionService();

    public ScheduleGenerationService(
            TripRepository tripRepository,
            CalendarRepository calendarRepository,
            CalendarDateRepository calendarDateRepository,
            BusRepository busRepository,
            DriverRepository driverRepository,
            ScheduleRepository scheduleRepository) {
        this.tripRepository         = tripRepository;
        this.calendarRepository     = calendarRepository;
        this.calendarDateRepository = calendarDateRepository;
        this.busRepository          = busRepository;
        this.driverRepository       = driverRepository;
        this.scheduleRepository     = scheduleRepository;
    }

    @Override
    public List<Schedule> generateSchedulesForDate(LocalDate date) {
        List<Schedule> generated = new ArrayList<>();

        // Bus et conducteurs disponibles — on les consomme au fur et a mesure
        Queue<Bus> availableBuses = new LinkedList<>(
                busRepository.findByStatus(BusStatus.AVAILABLE));
        Queue<Driver> availableDrivers = new LinkedList<>(
                driverRepository.findByStatus(DriverStatus.AVAILABLE));

        for (Trip trip : tripRepository.findAll()) {
            // Verifier si le service est actif ce jour
            Calendar calendar = calendarRepository
                    .findById(trip.getServiceId())
                    .orElse(null);
            if (calendar == null) continue;

            List<CalendarDate> exceptions = calendarDateRepository
                    .findByServiceId(trip.getServiceId());

            if (!calendarResolver.isServiceActiveOn(
                    calendar, exceptions, date)) {
                continue;
            }

            // Calculer les heures de debut et de fin du trip ce jour
            if (trip.getStopTimes().size() < 2) continue;

            List<StopTime> stopTimes = trip.getStopTimes();
            LocalTime firstArrival = stopTimes.get(0).getArrivalTime();
            LocalTime lastArrival  =
                    stopTimes.get(stopTimes.size() - 1).getArrivalTime();

            LocalDateTime startTime =
                    LocalDateTime.of(date, firstArrival);
            LocalDateTime endTime   =
                    LocalDateTime.of(date, lastArrival);

            // Gerer le passage minuit
            if (endTime.isBefore(startTime))
                endTime = endTime.plusDays(1);

            // Assigner un bus disponible
            Bus bus = availableBuses.poll();
            if (bus == null) {
                log.warn("Pas de bus disponible pour le trip {}",
                        trip.getId());
                continue;
            }

            // Assigner un conducteur disponible avec heures suffisantes
            double durationHours =
                    Duration.between(startTime, endTime)
                            .toMinutes() / 60.0;

            Driver driver = null;
            Queue<Driver> skipped = new LinkedList<>();
            while (!availableDrivers.isEmpty()) {
                Driver candidate = availableDrivers.poll();
                if (candidate.hasAvailableHoursFor(durationHours)) {
                    driver = candidate;
                    break;
                }
                skipped.add(candidate);
            }
            availableDrivers.addAll(skipped);

            if (driver == null) {
                log.warn("Pas de conducteur disponible pour le trip {}",
                        trip.getId());
                availableBuses.add(bus); // remettre le bus dans la file
                continue;
            }

            // Creer le Schedule
            Schedule schedule = Schedule.plan(
                    bus.getId(), driver.getId(),
                    trip.getRouteId(), trip.getId(),
                    startTime, endTime);
            scheduleRepository.save(schedule);
            generated.add(schedule);

            log.info("Schedule genere : trip={} bus={} driver={}",
                    trip.getId(), bus.getId(), driver.getId());
        }

        log.info("{} schedules generes pour le {}",
                generated.size(), date);
        return generated;
    }
}