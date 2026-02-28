// infrastructure/persistence/BusJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.BusStatus;
import jakarta.persistence.*;
import java.util.UUID;

/**
 * Entité JPA : représentation technique du Bus pour la base de données.
 * Complètement séparée du modèle domaine.
 * Si JPA évolue ou si tu changes de BDD, ton domaine est intact.
 */
@Entity
@Table(name = "buses")
public class BusJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String model;

    @Column(nullable = false)
    private int capacity;

    @Enumerated(EnumType.STRING) // Stocke "AVAILABLE" et non pas "0" — lisible en BDD
    @Column(nullable = false)
    private BusStatus status;

    @Column(nullable = false)
    private int mileage;

    @Column(name = "bus_number", nullable = false, unique = true)
    private String busNumber;

    // Constructeur no-arg requis par JPA
    protected BusJpaEntity() {}

    public BusJpaEntity(UUID id, String model, int capacity, BusStatus status, int mileage, String busNumber) {
        this.id = id;
        this.model = model;
        this.capacity = capacity;
        this.status = status;
        this.mileage = mileage;
        this.busNumber = busNumber;
    }

    // Getters
    public UUID getId() { return id; }
    public String getModel() { return model; }
    public int getCapacity() { return capacity; }
    public BusStatus getStatus() { return status; }
    public int getMileage() { return mileage; }
    public String getBusNumber() { return busNumber; }
}