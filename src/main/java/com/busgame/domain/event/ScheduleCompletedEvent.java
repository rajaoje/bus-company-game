// domain/event/ScheduleCompletedEvent.java
package com.busgame.domain.event;

import com.busgame.domain.model.*;
import java.time.LocalDateTime;

/**
 * Apres Niveau 2 GTFS : tripId remplace routeId.
 * Le handler utilise Trip pour calculer la distance
 * et la duree reelles du parcours.
 */
// domain/event/ScheduleCompletedEvent.java
public record ScheduleCompletedEvent(
        ScheduleId scheduleId,
        BusId busId,
        DriverId driverId,
        TripId tripId,           // Remplace RouteId
        double durationHours,
        LocalDateTime gameTime
) {}