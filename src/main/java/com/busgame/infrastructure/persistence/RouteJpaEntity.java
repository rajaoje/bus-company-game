// infrastructure/persistence/RouteJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.RouteType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// infrastructure/persistence/RouteJpaEntity.java
@Entity
@Table(name = "routes")
public class RouteJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "long_name")
    private String longName;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", nullable = false)
    private RouteType routeType;

    // Plus de @OneToMany stops — supprime en Niveau 2

    protected RouteJpaEntity() {}

    public RouteJpaEntity(UUID id, String name, String description,
                          String shortName, String longName,
                          RouteType routeType) {
        this.id        = id;
        this.name      = name;
        this.description = description;
        this.shortName = shortName;
        this.longName  = longName;
        this.routeType = routeType;
    }

    public UUID getId()              { return id; }
    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public String getShortName()     { return shortName; }
    public String getLongName()      { return longName; }
    public RouteType getRouteType()  { return routeType; }
}