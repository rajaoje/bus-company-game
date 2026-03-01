package com.busgame.infrastructure.web;

import com.busgame.domain.model.DirectionId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

// infrastructure/web/CreateTripRequest.java
public record CreateTripRequest(
        @NotNull UUID routeId,
        @NotNull UUID serviceId,    // ← ajout
        @NotNull DirectionId directionId,
        @NotBlank String headsign
) {}
