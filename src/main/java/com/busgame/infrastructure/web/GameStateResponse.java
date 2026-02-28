// infrastructure/web/GameStateResponse.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.SimulationStatus;
import java.time.LocalDateTime;

public record GameStateResponse(
        LocalDateTime currentGameTime,
        SimulationStatus status,
        int speedMultiplier,
        String description
) {}