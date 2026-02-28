// infrastructure/web/TripController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.*;
import com.busgame.domain.port.in.TripManagementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/trips")
public class TripController {

    private final TripManagementUseCase tripUseCase;
    private final TripResponseMapper responseMapper;

    public TripController(TripManagementUseCase tripUseCase,
                          TripResponseMapper responseMapper) {
        this.tripUseCase    = tripUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @Valid @RequestBody CreateTripRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMapper.toResponse(
                        tripUseCase.createTrip(
                                new RouteId(request.routeId()),
                                request.directionId(),
                                request.headsign()
                        )));
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<List<TripResponse>> getTripsByRoute(
            @PathVariable UUID routeId) {
        return ResponseEntity.ok(
                tripUseCase.getTripsByRoute(new RouteId(routeId))
                        .stream()
                        .map(responseMapper::toResponse)
                        .toList()
        );
    }

    @PostMapping("/{tripId}/stop-times")
    public ResponseEntity<TripResponse> addStopTime(
            @PathVariable UUID tripId,
            @Valid @RequestBody AddStopTimeRequest request) {
        return ResponseEntity.ok(responseMapper.toResponse(
                tripUseCase.addStopTime(
                        new TripId(tripId),
                        new StopId(request.stopId()),
                        request.arrivalTime(),
                        request.departureTime(),
                        request.distanceFromPreviousKm()
                )));
    }

    @DeleteMapping("/{tripId}/stop-times/{stopTimeId}")
    public ResponseEntity<TripResponse> removeStopTime(
            @PathVariable UUID tripId,
            @PathVariable UUID stopTimeId) {
        return ResponseEntity.ok(responseMapper.toResponse(
                tripUseCase.removeStopTime(
                        new TripId(tripId),
                        new StopTimeId(stopTimeId)
                )));
    }
}