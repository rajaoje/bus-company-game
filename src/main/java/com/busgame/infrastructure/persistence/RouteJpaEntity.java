// infrastructure/persistence/RouteJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.RouteType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    /**
     * La relation One-to-Many avec les arrets.
     * CascadeType.ALL : toute operation sur Route se propage aux Stop.
     * orphanRemoval : un Stop retire de la liste est supprime en BDD.
     * Ces deux options ensemble garantissent que Route reste le seul
     * point de controle de ses arrets — exactement comme dans le domaine.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "route_id")
    private List<StopJpaEntity> stops = new ArrayList<>();

    protected RouteJpaEntity() {}

    public RouteJpaEntity(UUID id, String name, String description,
                          String shortName, String longName, RouteType routeType) { 
        this.id = id;
        this.name = name;
        this.description = description;
        this.shortName = shortName;
        this.longName = longName;
        this.routeType = routeType;
    }

    public void setStops(List<StopJpaEntity> stops) {
        this.stops.clear();
        this.stops.addAll(stops);

    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<StopJpaEntity> getStops() { return stops; }
    public String getShortName() { return shortName; }
    public String getLongName() { return longName; }
    public RouteType getRouteType() { return routeType; }

    public void setId(UUID value) {}


    public void setName(String name) {
    }

    public void setDescription(String description) {
    }

    public void setShortName(String shortName) {
    }

    public void setLongName(String longName) {
    }

    public void setRouteType(RouteType routeType) {
    }
}