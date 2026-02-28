package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalTime;
import java.util.UUID;

public record AddStopTimeRequest(
        @NotNull UUID stopId,
        @NotNull LocalTime arrivalTime,
        @NotNull LocalTime departureTime,
        @PositiveOrZero double distanceFromPreviousKm
) {}
