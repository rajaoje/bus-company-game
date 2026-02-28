// domain/model/StopTime.java
package com.busgame.domain.model;

import java.time.LocalTime;

/**
 * GTFS : stop_times.txt
 *
 * Associe un arret (Stop) a un passage (Trip) avec les horaires
 * d'arrivee et de depart. C'est ici que vit la sequence des arrets
 * et la distance depuis le precedent.
 */
public class StopTime {

    private final StopTimeId id;
    private final StopId stopId;
    private int stopSequence;          // GTFS : stop_sequence
    private LocalTime arrivalTime;     // GTFS : arrival_time
    private LocalTime departureTime;   // GTFS : departure_time
    private Distance distanceFromPrevious; // GTFS : shape_dist_traveled

    // Constructeur package-private — seul Trip peut creer des StopTime
    StopTime(StopTimeId id, StopId stopId, int stopSequence,
             LocalTime arrivalTime, LocalTime departureTime,
             Distance distanceFromPrevious) {
        this.id                   = id;
        this.stopId               = stopId;
        this.stopSequence         = stopSequence;
        this.arrivalTime          = arrivalTime;
        this.departureTime        = departureTime;
        this.distanceFromPrevious = distanceFromPrevious;
    }

    public static StopTime reconstitute(StopTimeId id, StopId stopId,
                                        int stopSequence,
                                        LocalTime arrivalTime,
                                        LocalTime departureTime,
                                        Distance distanceFromPrevious) {
        return new StopTime(id, stopId, stopSequence,
                arrivalTime, departureTime,
                distanceFromPrevious);
    }

    public StopTimeId getId()               { return id; }
    public StopId getStopId()               { return stopId; }
    public int getStopSequence()            { return stopSequence; }
    public LocalTime getArrivalTime()       { return arrivalTime; }
    public LocalTime getDepartureTime()     { return departureTime; }
    public Distance getDistanceFromPrevious() { return distanceFromPrevious; }

    // Package-private — utilise par Trip lors du reordonnancement
    void setStopSequence(int stopSequence) {
        this.stopSequence = stopSequence;
    }
}