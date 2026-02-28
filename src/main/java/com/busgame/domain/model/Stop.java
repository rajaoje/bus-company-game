// domain/model/Stop.java
package com.busgame.domain.model;

import java.util.UUID;

/**
 * GTFS : stops.txt
 *
 * Stop est maintenant un agregat independant — il n'appartient
 * plus a une Route. Un meme arret physique peut etre partage
 * par plusieurs lignes via StopTime.
 */
public class Stop {

    private final StopId id;
    private String name;
    private double latitude;   // GTFS : stop_lat
    private double longitude;  // GTFS : stop_lon

    private Stop(StopId id, String name,
                 double latitude, double longitude) {
        this.id        = id;
        this.name      = name;
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public static Stop create(String name,
                              double latitude, double longitude) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException(
                    "Le nom de l'arret est obligatoire.");
        return new Stop(new StopId(UUID.randomUUID()),
                name, latitude, longitude);
    }

    public static Stop reconstitute(StopId id, String name,
                                    double latitude, double longitude) {
        return new Stop(id, name, latitude, longitude);
    }

    public StopId getId()      { return id; }
    public String getName()    { return name; }
    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }
}