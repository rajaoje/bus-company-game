// domain/port/in/TripManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.*;

import java.time.LocalTime;
import java.util.List;

public interface TripManagementUseCase {
    Trip createTrip(RouteId routeId, DirectionId directionId, String headsign);
    Trip getTrip(TripId id);
    List<Trip> getTripsByRoute(RouteId routeId);
    Trip addStopTime(TripId tripId, StopId stopId,
                     LocalTime arrivalTime, LocalTime departureTime,
                     double distanceFromPreviousKm);
    Trip removeStopTime(TripId tripId, StopTimeId stopTimeId);
}