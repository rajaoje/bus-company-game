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
     * Mappe tous les champs Route incluant les nouveaux champs GTFS Niveau 1
     * (shortName, longName, routeType) et les coordonnees GPS des stops.
     *
     * Apres Niveau 2 : Route ne contient plus de stops directement —
     * la liste des stopTimes est portee par Trip.
     * Cette methode ne mappe donc plus de stops.
     */
    public RouteJpaEntity toEntity(Route route) {
        return new RouteJpaEntity(
                route.getId().value(),
                route.getName(),
                route.getDescription(),
                route.getShortName(),
                route.getLongName(),
                route.getRouteType()
        );
    }

    /**
     * JPA → Domaine.
     * Reconstruit une Route depuis son entite JPA.
     * Apres Niveau 2, Route n'a plus de stops — pas besoin
     * de charger des entites enfants ici.
     */
    public Route toDomain(RouteJpaEntity entity) {
        return Route.reconstitute(
                new RouteId(entity.getId()),
                entity.getName(),
                entity.getDescription(),
                entity.getShortName(),
                entity.getLongName(),
                entity.getRouteType()
        );
    }
}