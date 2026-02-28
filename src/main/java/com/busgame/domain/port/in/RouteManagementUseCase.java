// domain/port/in/RouteManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.Distance;
import com.busgame.domain.model.Route;
import com.busgame.domain.model.RouteId;
import com.busgame.domain.model.StopId;

import java.util.List;

public interface RouteManagementUseCase {
    Route getRoute(RouteId id);
    List<Route> getAllRoutes();
    Route removeStop(RouteId routeId, StopId stopId);
    Route createRoute(String name, String description, String shortName);
    Route addStop(RouteId routeId, String name,
                  Distance distanceFromPrevious,
                  double latitude, double longitude);
}