// infrastructure/persistence/RouteMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class RouteMapper {

    /**
     * Domaine → JPA.
     * Mappe tous les champs incluant les nouveaux champs GTFS
     * et les coordonnees GPS des arrets.
     */
    public RouteJpaEntity toEntity(Route route) {
        RouteJpaEntity entity = new RouteJpaEntity(
                route.getId().value(),
                route.getName(),
                route.getDescription(),
                route.getShortName(),   // GTFS : route_short_name
                route.getLongName(),    // GTFS : route_long_name
                route.getRouteType()    // GTFS : route_type
        );

        List<StopJpaEntity> stopEntities = route.getStops()
                .stream()
                .map(stop -> stopToEntity(stop, route.getId().value()))
                .toList();

        entity.setStops(stopEntities);
        return entity;
    }

    /**
     * JPA → Domaine.
     * Les stops sont charges via @OneToMany dans RouteJpaEntity —
     * pas besoin de les passer separement.
     */
    public Route toDomain(RouteJpaEntity entity) {
        List<Stop> stops = entity.getStops()
                .stream()
                .sorted(Comparator.comparingInt(StopJpaEntity::getSequenceOrder))
                .map(this::stopToDomain)
                .toList();

        return Route.reconstitute(
                new RouteId(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getShortName(),   // GTFS : route_short_name
                entity.getLongName(),    // GTFS : route_long_name
                entity.getRouteType(),   // GTFS : route_type
                stops
        );
    }

    /**
     * Stop Domaine → Stop JPA.
     * Inclut les coordonnees GPS ajoutees au Niveau 1 GTFS.
     */
    public StopJpaEntity stopToEntity(Stop stop, java.util.UUID routeId) {
        return new StopJpaEntity(
                stop.getId().value(),
                routeId,
                stop.getName(),
                stop.getSequenceOrder(),
                stop.getDistanceFromPrevious().kilometers(),
                stop.getLatitude(),     // GTFS : stop_lat
                stop.getLongitude()     // GTFS : stop_lon
        );
    }

    /**
     * Stop JPA → Stop Domaine.
     * Reconstruit l'entite avec les coordonnees GPS.
     */
    private Stop stopToDomain(StopJpaEntity entity) {
        return Stop.reconstitute(
                new StopId(entity.getId()),
                entity.getName(),
                entity.getSequenceOrder(),
                new Distance(entity.getDistanceFromPreviousKm()),
                entity.getLatitude(),   // GTFS : stop_lat
                entity.getLongitude()   // GTFS : stop_lon
        );
    }
}