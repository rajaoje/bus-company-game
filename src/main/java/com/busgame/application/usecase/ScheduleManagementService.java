// application/usecase/ScheduleManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.*;
import com.busgame.domain.model.*;
import com.busgame.domain.port.in.ScheduleManagementUseCase;
import com.busgame.domain.port.out.*;
import com.busgame.domain.service.ScheduleValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Use Case de planification des horaires.
 *
 * C'est la classe la plus "lourde" qu'on ait ecrite jusqu'ici,
 * parce qu'elle coordonne quatre repositories et un Domain Service.
 * Mais remarque que chaque etape reste simple et lisible —
 * la complexite est distribuee, pas concentree.
 */
// application/usecase/ScheduleManagementService.java
@Service
@Transactional
public class ScheduleManagementService implements ScheduleManagementUseCase {

    private final ScheduleRepository scheduleRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;

    private final ScheduleValidationService validationService =
            new ScheduleValidationService();

    public ScheduleManagementService(ScheduleRepository scheduleRepository,
                                     BusRepository busRepository,
                                     DriverRepository driverRepository,
                                     RouteRepository routeRepository,
                                     TripRepository tripRepository) {
        this.scheduleRepository = scheduleRepository;
        this.busRepository      = busRepository;
        this.driverRepository   = driverRepository;
        this.routeRepository    = routeRepository;
        this.tripRepository     = tripRepository;
    }

    @Override
    public Schedule planSchedule(BusId busId, DriverId driverId,
                                 RouteId routeId, TripId tripId,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException(busId));
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new DriverNotFoundException(driverId));
        routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        // Verifier que le Trip existe et appartient a la Route
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new TripNotFoundException(tripId));
        if (!trip.getRouteId().equals(routeId))
            throw new IllegalArgumentException(
                    "Le trip " + tripId + " n'appartient pas a la route " + routeId);

        double durationHours =
                Duration.between(startTime, endTime).toMinutes() / 60.0;

        validationService.validateBusAvailability(bus);
        validationService.validateDriverAvailability(driver, durationHours);
        checkBusOverlap(busId, startTime, endTime);
        checkDriverOverlap(driverId, startTime, endTime);

        Schedule schedule = Schedule.plan(
                busId, driverId, routeId, tripId, startTime, endTime);
        return scheduleRepository.save(schedule);
    }

    // ... reste inchange (getSchedule, getAllSchedules, etc.)

    private void checkBusOverlap(BusId busId,
                                 LocalDateTime startTime,
                                 LocalDateTime endTime) {
        if (!scheduleRepository
                .findActiveSchedulesForBusInPeriod(busId, startTime, endTime)
                .isEmpty())
            throw new IllegalStateException(
                    "Le bus a deja un horaire sur cette plage horaire.");
    }

    private void checkDriverOverlap(DriverId driverId,
                                    LocalDateTime startTime,
                                    LocalDateTime endTime) {
        if (!scheduleRepository
                .findActiveSchedulesForDriverInPeriod(
                        driverId, startTime, endTime)
                .isEmpty())
            throw new IllegalStateException(
                    "Le conducteur a deja un horaire sur cette plage horaire.");
    }

    @Override
    @Transactional(readOnly = true)
    public Schedule getSchedule(ScheduleId id) {
        return scheduleRepository.findById(id)
                .orElseThrow(() -> new ScheduleNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesForBus(BusId busId) {
        return scheduleRepository.findByBusId(busId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesForDriver(DriverId driverId) {
        return scheduleRepository.findByDriverId(driverId);
    }

    @Override
    public Schedule cancelSchedule(ScheduleId id) {
        Schedule schedule = getSchedule(id);
        schedule.cancel();
        return scheduleRepository.save(schedule);
    }
}