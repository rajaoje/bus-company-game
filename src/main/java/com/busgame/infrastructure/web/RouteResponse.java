// infrastructure/web/RouteResponse.java
package com.busgame.infrastructure.web;

import java.util.UUID;

/**
 * DTO de reponse pour une Route.
 *
 * Apres Niveau 2 GTFS : plus de liste de stops ici.
 * Pour obtenir les stops d'une route, le client doit appeler
 * GET /api/v1/trips/route/{routeId} qui retourne les Trip
 * avec leurs StopTime et les infos de chaque Stop.
 */
public record RouteResponse(
        UUID id,
        String shortName,       // GTFS : route_short_name — ex: "12"
        String name,
        String longName,        // GTFS : route_long_name
        String description,
        String routeType        // GTFS : route_type — ex: "BUS"
) {}