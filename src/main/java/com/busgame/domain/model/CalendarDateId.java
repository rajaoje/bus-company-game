// domain/model/CalendarDateId.java
package com.busgame.domain.model;

import java.util.UUID;

public record CalendarDateId(UUID value) {
    public CalendarDateId {
        if (value == null)
            throw new IllegalArgumentException(
                    "CalendarDateId ne peut pas etre null.");
    }
}