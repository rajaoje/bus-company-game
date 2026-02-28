// infrastructure/web/RouteResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Route;
import org.springframework.stereotype.Component;

@Component
public class RouteResponseMapper {

    public RouteResponse toResponse(Route route) {
        var stopResponses = route.getStops().stream()
                .map(s -> new StopResponse(
                        s.getId().value(),
                        s.getName(),
                        s.getSequenceOrder(),
                        s.getDistanceFromPrevious().kilometers(),
                        s.getLatitude(),    // GTFS : stop_lat
                        s.getLongitude()    // GTFS : stop_lon
                ))
                .toList();
        return new RouteResponse(
                route.getId().value(),
                route.getShortName(),
                route.getName(),
                route.getLongName(),
                route.getDescription(),
                route.getRouteType().name(),
                stopResponses,
                route.getTotalDistance().kilometers(),
                route.getEstimatedDurationMinutes()
        );
    }
}