// domain/event/ScheduleStartedEvent.java
package com.busgame.domain.event;

import com.busgame.domain.model.ScheduleId;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.DriverId;
import java.time.LocalDateTime;

/**
 * Evenement publie quand un horaire demarre.
 * Les handlers vont reagir en mettant a jour Bus et Driver.
 */
public record ScheduleStartedEvent(
        ScheduleId scheduleId,
        BusId busId,
        DriverId driverId,
        LocalDateTime gameTime
) {}