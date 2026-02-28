// domain/model/ScheduleId.java
package com.busgame.domain.model;

import java.util.UUID;

public record ScheduleId(UUID value) {
    public ScheduleId {
        if (value == null) throw new IllegalArgumentException("ScheduleId ne peut pas etre null.");
    }
}