// infrastructure/web/RouteResponse.java
package com.busgame.infrastructure.web;

import java.util.List;
import java.util.UUID;

public record RouteResponse(
        UUID id,
        String shortName,               // GTFS : route_short_name — ex: "12"
        String name,
        String longName,                // GTFS : route_long_name
        String description,
        String routeType,               // GTFS : route_type — ex: "BUS"
        List<StopResponse> stops,
        double totalDistanceKm,
        double estimatedDurationMinutes
) {}