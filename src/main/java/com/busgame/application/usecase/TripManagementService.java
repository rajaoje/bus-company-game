// application/usecase/TripManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.RouteNotFoundException;
import com.busgame.domain.exception.ServiceNotFoundException;
import com.busgame.domain.model.*;
import com.busgame.domain.port.in.TripManagementUseCase;
import com.busgame.domain.port.out.CalendarRepository;
import com.busgame.domain.port.out.RouteRepository;
import com.busgame.domain.port.out.StopRepository;
import com.busgame.domain.port.out.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class TripManagementService implements TripManagementUseCase {

    private final TripRepository  tripRepository;
    private final RouteRepository routeRepository;
    private final StopRepository  stopRepository;
    private final CalendarRepository calendarRepository;

    public TripManagementService(TripRepository tripRepository,
                                 RouteRepository routeRepository,
                                 StopRepository stopRepository,
                                 CalendarRepository calendarRepository) {
        this.tripRepository  = tripRepository;
        this.routeRepository = routeRepository;
        this.stopRepository  = stopRepository;
        this.calendarRepository = calendarRepository;
    }

    @Override
    public Trip createTrip(RouteId routeId, ServiceId serviceId,
                           DirectionId directionId, String headsign) {
        routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));

        // Verifier que le Calendar existe
        calendarRepository.findById(serviceId)
                .orElseThrow(() -> new ServiceNotFoundException(serviceId));

        Trip trip = Trip.create(routeId, serviceId, directionId, headsign);
        return tripRepository.save(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public Trip getTrip(TripId id) {
        return tripRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Trip introuvable : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getTripsByRoute(RouteId routeId) {
        return tripRepository.findByRouteId(routeId);
    }

    @Override
    public Trip addStopTime(TripId tripId, StopId stopId,
                            LocalTime arrivalTime,
                            LocalTime departureTime,
                            double distanceFromPreviousKm) {
        Trip trip = getTrip(tripId);

        // Verifier que le stop existe
        stopRepository.findById(stopId)
                .orElseThrow(() -> new RuntimeException(
                        "Arret introuvable : " + stopId));

        trip.addStopTime(stopId, arrivalTime, departureTime,
                new Distance(distanceFromPreviousKm));
        return tripRepository.save(trip);
    }

    @Override
    public Trip removeStopTime(TripId tripId, StopTimeId stopTimeId) {
        Trip trip = getTrip(tripId);
        trip.removeStopTime(stopTimeId);
        return tripRepository.save(trip);
    }
}