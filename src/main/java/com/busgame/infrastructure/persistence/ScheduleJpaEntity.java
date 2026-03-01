// infrastructure/persistence/ScheduleJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.ScheduleStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

// infrastructure/persistence/ScheduleJpaEntity.java
@Entity
@Table(name = "schedules")
public class ScheduleJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "bus_id", nullable = false)
    private UUID busId;

    @Column(name = "driver_id", nullable = false)
    private UUID driverId;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Column(name = "trip_id")   // nullable — migration progressive
    private UUID tripId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleStatus status;

    protected ScheduleJpaEntity() {}

    public ScheduleJpaEntity(UUID id, UUID busId, UUID driverId,
                             UUID routeId, UUID tripId,
                             LocalDateTime startTime, LocalDateTime endTime,
                             ScheduleStatus status) {
        this.id        = id;
        this.busId     = busId;
        this.driverId  = driverId;
        this.routeId   = routeId;
        this.tripId    = tripId;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.status    = status;
    }

    public UUID getId()                    { return id; }
    public UUID getBusId()                 { return busId; }
    public UUID getDriverId()              { return driverId; }
    public UUID getRouteId()               { return routeId; }
    public UUID getTripId()                { return tripId; }
    public LocalDateTime getStartTime()    { return startTime; }
    public LocalDateTime getEndTime()      { return endTime; }
    public ScheduleStatus getStatus()      { return status; }
}