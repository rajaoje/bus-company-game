// infrastructure/persistence/TripJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.DirectionId;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "trips")
public class TripJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "route_id", nullable = false)
    private UUID routeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction_id", nullable = false)
    private DirectionId directionId;

    @Column(name = "headsign", nullable = false)
    private String headsign;

    @OneToMany(cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "trip_id")
    private List<StopTimeJpaEntity> stopTimes = new ArrayList<>();

    protected TripJpaEntity() {}

    public TripJpaEntity(UUID id, UUID routeId,
                         DirectionId directionId, String headsign) {
        this.id          = id;
        this.routeId     = routeId;
        this.directionId = directionId;
        this.headsign    = headsign;
    }

    public void setStopTimes(List<StopTimeJpaEntity> stopTimes) {
        this.stopTimes.clear();
        this.stopTimes.addAll(stopTimes);
    }

    public UUID getId()                { return id; }
    public UUID getRouteId()           { return routeId; }
    public DirectionId getDirectionId() { return directionId; }
    public String getHeadsign()        { return headsign; }
    public List<StopTimeJpaEntity> getStopTimes() { return stopTimes; }
}