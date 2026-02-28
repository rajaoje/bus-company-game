// domain/model/StopId.java
package com.busgame.domain.model;

import java.util.UUID;

public record StopId(UUID value) {
    public StopId {
        if (value == null) throw new IllegalArgumentException("StopId ne peut pas etre null.");
    }
}