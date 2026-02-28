// infrastructure/web/RouteController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Distance;
import com.busgame.domain.model.RouteId;
import com.busgame.domain.model.StopId;
import com.busgame.domain.port.in.RouteManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/routes")
public class RouteController {

    private final RouteManagementUseCase routeUseCase;
    private final RouteResponseMapper responseMapper;

    public RouteController(RouteManagementUseCase routeUseCase,
                           RouteResponseMapper responseMapper) {
        this.routeUseCase = routeUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<RouteResponse> createRoute(@RequestBody CreateRouteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMapper.toResponse(
                        routeUseCase.createRoute(request.name(), request.description(), request.shortName())));
    }

    @GetMapping
    public ResponseEntity<List<RouteResponse>> getAllRoutes() {
        return ResponseEntity.ok(
                routeUseCase.getAllRoutes().stream().map(responseMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponse> getRoute(@PathVariable UUID id) {
        return ResponseEntity.ok(
                responseMapper.toResponse(routeUseCase.getRoute(new RouteId(id))));
    }

    @PostMapping("/{id}/stops")
    public ResponseEntity<RouteResponse> addStop(@PathVariable UUID id,
                                                 @RequestBody AddStopRequest request) {
        return ResponseEntity.ok(responseMapper.toResponse(
                routeUseCase.addStop(new RouteId(id), request.name(),
                        new Distance(request.distanceFromPreviousKm()), request.latitude(), request.longitude())));
    }

    @DeleteMapping("/{routeId}/stops/{stopId}")
    public ResponseEntity<RouteResponse> removeStop(@PathVariable UUID routeId,
                                                    @PathVariable UUID stopId) {
        return ResponseEntity.ok(responseMapper.toResponse(
                routeUseCase.removeStop(new RouteId(routeId), new StopId(stopId))));
    }
}