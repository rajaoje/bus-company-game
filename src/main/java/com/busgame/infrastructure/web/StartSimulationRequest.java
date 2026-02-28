// infrastructure/web/StartSimulationRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record StartSimulationRequest(
        @NotNull(message = "La date de debut est obligatoire")
        LocalDateTime startTime
) {}