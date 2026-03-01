// infrastructure/web/RouteResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Route;
import org.springframework.stereotype.Component;

/**
 * Mappe une Route domaine vers son DTO de reponse HTTP.
 *
 * Apres Niveau 2 GTFS : Route ne contient plus ses stops directement.
 * Le mapper est donc allege — il ne mappe plus de stops.
 * Les stops sont accessibles via les Trip de la route
 * (TripResponseMapper s'en charge).
 */
@Component
public class RouteResponseMapper {

    public RouteResponse toResponse(Route route) {
        return new RouteResponse(
                route.getId().value(),
                route.getShortName(),       // GTFS : route_short_name
                route.getName(),
                route.getLongName(),        // GTFS : route_long_name
                route.getDescription(),
                route.getRouteType().name() // GTFS : route_type — ex: "BUS"
        );
    }
}