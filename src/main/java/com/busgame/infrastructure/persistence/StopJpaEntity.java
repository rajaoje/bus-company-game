// infrastructure/persistence/StopJpaEntity.java
package com.busgame.infrastructure.persistence;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "stops")
public class StopJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    protected StopJpaEntity() {}

    public StopJpaEntity(UUID id, String name,
                         double latitude, double longitude) {
        this.id        = id;
        this.name      = name;
        this.latitude  = latitude;
        this.longitude = longitude;
    }

    public UUID getId()        { return id; }
    public String getName()    { return name; }
    public double getLatitude()  { return latitude; }
    public double getLongitude() { return longitude; }
}