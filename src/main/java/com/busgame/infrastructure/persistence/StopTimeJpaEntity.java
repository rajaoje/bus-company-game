// infrastructure/persistence/StopTimeJpaEntity.java
package com.busgame.infrastructure.persistence;

import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "stop_times")
public class StopTimeJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "trip_id", nullable = false)
    private UUID tripId;

    @Column(name = "stop_id", nullable = false)
    private UUID stopId;

    @Column(name = "stop_sequence", nullable = false)
    private int stopSequence;

    @Column(name = "arrival_time", nullable = false)
    private LocalTime arrivalTime;

    @Column(name = "departure_time", nullable = false)
    private LocalTime departureTime;

    @Column(name = "distance_from_previous_km", nullable = false)
    private double distanceFromPreviousKm;

    protected StopTimeJpaEntity() {}

    public StopTimeJpaEntity(UUID id, UUID tripId, UUID stopId,
                             int stopSequence,
                             LocalTime arrivalTime,
                             LocalTime departureTime,
                             double distanceFromPreviousKm) {
        this.id                     = id;
        this.tripId                 = tripId;
        this.stopId                 = stopId;
        this.stopSequence           = stopSequence;
        this.arrivalTime            = arrivalTime;
        this.departureTime          = departureTime;
        this.distanceFromPreviousKm = distanceFromPreviousKm;
    }

    public UUID getId()              { return id; }
    public UUID getTripId()          { return tripId; }
    public UUID getStopId()          { return stopId; }
    public int getStopSequence()     { return stopSequence; }
    public LocalTime getArrivalTime()    { return arrivalTime; }
    public LocalTime getDepartureTime()  { return departureTime; }
    public double getDistanceFromPreviousKm() { return distanceFromPreviousKm; }
}