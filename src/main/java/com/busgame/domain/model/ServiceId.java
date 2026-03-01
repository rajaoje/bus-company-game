// domain/model/ServiceId.java
package com.busgame.domain.model;

import java.util.UUID;

/**
 * GTFS : service_id
 * Identifiant commun entre Calendar et CalendarDate.
 */
public record ServiceId(UUID value) {
    public ServiceId {
        if (value == null)
            throw new IllegalArgumentException(
                    "ServiceId ne peut pas etre null.");
    }
}