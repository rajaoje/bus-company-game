// domain/model/MaintenanceRecordId.java
package com.busgame.domain.model;

import java.util.UUID;

public record MaintenanceRecordId(UUID value) {
    public MaintenanceRecordId {
        if (value == null)
            throw new IllegalArgumentException("MaintenanceRecordId ne peut pas etre null.");
    }
}