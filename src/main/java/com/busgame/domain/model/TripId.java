// domain/model/TripId.java
package com.busgame.domain.model;

import java.util.UUID;

public record TripId(UUID value) {
    public TripId {
        if (value == null)
            throw new IllegalArgumentException("TripId ne peut pas etre null.");
    }
}