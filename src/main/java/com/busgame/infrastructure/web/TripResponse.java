package com.busgame.infrastructure.web;

import com.busgame.domain.model.DirectionId;

import java.util.List;
import java.util.UUID;

public record TripResponse(
        UUID id,
        UUID routeId,
        DirectionId directionId,
        String headsign,
        List<StopTimeResponse> stopTimes,
        double totalDistanceKm,
        double estimatedDurationMinutes
) {}
