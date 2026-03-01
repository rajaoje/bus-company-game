// infrastructure/web/PlanScheduleRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

// infrastructure/web/PlanScheduleRequest.java
public record PlanScheduleRequest(
        @NotNull UUID busId,
        @NotNull UUID driverId,
        @NotNull UUID routeId,
        @NotNull UUID tripId,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime
) {}