// infrastructure/web/ScheduleResponse.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.ScheduleStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record ScheduleResponse(
        UUID id,
        UUID busId,
        UUID driverId,
        UUID routeId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ScheduleStatus status,
        double durationHours
) {}