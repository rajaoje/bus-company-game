// domain/model/DriverId.java
package com.busgame.domain.model;

import java.util.UUID;

/**
 * Value Object pour l'identité d'un conducteur.
 * Même pattern que BusId — on encapsule l'UUID pour éviter
 * toute confusion entre les identifiants dans le système.
 */
public record DriverId(UUID value) {
    public DriverId {
        if (value == null) throw new IllegalArgumentException("DriverId ne peut pas etre null.");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}