package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record CreateStopRequest(
        @NotBlank String name,
        double latitude,
        double longitude
) {}