// domain/model/StopTimeId.java
package com.busgame.domain.model;

import java.util.UUID;

public record StopTimeId(UUID value) {
    public StopTimeId {
        if (value == null)
            throw new IllegalArgumentException("StopTimeId ne peut pas etre null.");
    }
}