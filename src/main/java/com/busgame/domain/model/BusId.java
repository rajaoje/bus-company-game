// domain/model/BusId.java
package com.busgame.domain.model;

import java.util.UUID;

/**
 * Value Object : représente l'identité d'un bus.
 * En DDD, on encapsule les IDs dans des Value Objects pour éviter
 * de confondre un UUID de bus avec un UUID de conducteur par exemple.
 * Java 21 nous permet d'utiliser un record, qui est parfait ici :
 * immuable, equals/hashCode automatiques.
 */
public record BusId(UUID value) {
    public BusId {
        // Validation dans le compact constructor du record
        if (value == null) throw new IllegalArgumentException("BusId ne peut pas être null.");
    }

    @Override
    public String toString() {
        return value.toString();
    }
}