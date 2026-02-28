// infrastructure/web/PlanScheduleRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.UUID;

public record PlanScheduleRequest(
        @NotNull(message = "L'identifiant du bus est obligatoire")
        UUID busId,

        @NotNull(message = "L'identifiant du conducteur est obligatoire")
        UUID driverId,

        @NotNull(message = "L'identifiant du parcours est obligatoire")
        UUID routeId,

        @NotNull(message = "L'heure de debut est obligatoire")
        LocalDateTime startTime,

        @NotNull(message = "L'heure de fin est obligatoire")
        LocalDateTime endTime
) {}