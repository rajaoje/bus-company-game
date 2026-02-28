// domain/event/ScheduleCompletedEvent.java
package com.busgame.domain.event;

import com.busgame.domain.model.ScheduleId;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.DriverId;
import com.busgame.domain.model.RouteId;
import java.time.LocalDateTime;

/**
 * Evenement publie quand un horaire se termine normalement.
 * Les handlers vont : remettre le bus disponible, mettre a jour
 * les heures du conducteur, ajouter du kilometrage au bus.
 */
public record ScheduleCompletedEvent(
        ScheduleId scheduleId,
        BusId busId,
        DriverId driverId,
        RouteId routeId,
        double durationHours,
        LocalDateTime gameTime
) {}