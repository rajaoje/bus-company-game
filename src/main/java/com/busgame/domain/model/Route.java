// domain/model/Route.java — version allégée
package com.busgame.domain.model;

import java.util.UUID;

/**
 * GTFS : routes.txt
 *
 * Route definit une ligne de bus — son nom, son type, son identite.
 * Elle ne contient plus ses arrets directement.
 * Les arrets sont associes via Trip → StopTime → Stop.
 */
public class Route {

    private final RouteId id;
    private String name;
    private String description;
    private String shortName;      // GTFS : route_short_name
    private String longName;       // GTFS : route_long_name
    private RouteType routeType;   // GTFS : route_type

    private Route(RouteId id, String name, String description,
                  String shortName, RouteType routeType) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.shortName   = shortName != null ? shortName : name;
        this.longName    = name;
        this.routeType   = routeType;
    }

    public static Route create(String name, String description,
                               String shortName) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException(
                    "Le nom du parcours est obligatoire.");
        return new Route(new RouteId(UUID.randomUUID()),
                name, description, shortName, RouteType.BUS);
    }

    public static Route reconstitute(RouteId id, String name,
                                     String description,
                                     String shortName,
                                     String longName,
                                     RouteType routeType) {
        Route route = new Route(id, name, description,
                shortName, routeType);
        route.longName = longName;
        return route;
    }

    public RouteId getId()           { return id; }
    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public String getShortName()     { return shortName; }
    public String getLongName()      { return longName; }
    public RouteType getRouteType()  { return routeType; }
}