package com.busgame.infrastructure.web;

import java.time.LocalTime;
import java.util.UUID;

public record StopTimeResponse(
        UUID id,
        UUID stopId,
        String stopName,
        int stopSequence,
        LocalTime arrivalTime,
        LocalTime departureTime,
        double distanceFromPreviousKm
) {}
