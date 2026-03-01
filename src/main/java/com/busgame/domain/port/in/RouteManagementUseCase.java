// domain/port/in/RouteManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.Route;
import com.busgame.domain.model.RouteId;

import java.util.List;

/**
 * Apres Niveau 2 GTFS : le use case Route est allege.
 * addStop() et removeStop() ont ete retires — ils sont
 * desormais dans TripManagementUseCase sous addStopTime()
 * et removeStopTime().
 */
public interface RouteManagementUseCase {
    Route createRoute(String name, String description, String shortName);
    Route getRoute(RouteId id);
    List<Route> getAllRoutes();
    void deleteRoute(RouteId id);
}