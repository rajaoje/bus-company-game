// domain/port/out/TripRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.RouteId;
import com.busgame.domain.model.Trip;
import com.busgame.domain.model.TripId;

import java.util.List;
import java.util.Optional;

public interface TripRepository {
    Trip save(Trip trip);
    Optional<Trip> findById(TripId id);
    List<Trip> findAll();
    List<Trip> findByRouteId(RouteId routeId);
    void delete(TripId id);
}