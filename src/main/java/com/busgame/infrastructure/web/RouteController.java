// infrastructure/web/RouteController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.RouteId;
import com.busgame.domain.port.in.RouteManagementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Apres Niveau 2 GTFS : RouteController est allege.
 * - Plus de addStop() / removeStop() — ces endpoints
 *   sont desormais dans TripController via addStopTime().
 * - Route ne gere plus que son identite.
 */
@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteManagementUseCase routeUseCase;
    private final RouteResponseMapper responseMapper;

    public RouteController(RouteManagementUseCase routeUseCase,
                           RouteResponseMapper responseMapper) {
        this.routeUseCase   = routeUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(
            @Valid @RequestBody CreateRouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMapper.toResponse(
                        routeUseCase.createRoute(
                                request.name(),
                                request.description(),
                                request.shortName()
                        )));
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(
                routeUseCase.getAllRoutes()
                        .stream()
                        .map(responseMapper::toResponse)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getRoute(
            @PathVariable UUID id) {
        return ResponseEntity.ok(
                responseMapper.toResponse(
                        routeUseCase.getRoute(new RouteId(id))
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(
            @PathVariable UUID id) {
        routeUseCase.deleteRoute(new RouteId(id));
        return ResponseEntity.noContent().build();
    }
}