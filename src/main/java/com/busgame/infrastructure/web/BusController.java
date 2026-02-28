// infrastructure/web/BusController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.BusStatus;
import com.busgame.domain.port.in.FleetManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Adapter Web : expose les use cases via une API REST.
 * Ce contrôleur ne contient AUCUNE logique métier — il traduit
 * les requêtes HTTP en appels aux use cases, et les résultats
 * en réponses HTTP.
 */
@RestController
@RequestMapping("/api/v1/fleet")
public class BusController {

    private final FleetManagementUseCase fleetUseCase;
    private final BusResponseMapper responseMapper;

    public BusController(FleetManagementUseCase fleetUseCase, BusResponseMapper responseMapper) {
        this.fleetUseCase = fleetUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping("/buses")
    public ResponseEntity<BusResponse> registerBus(@RequestBody RegisterBusRequest request) {
        Bus bus = fleetUseCase.registerBus(request.model(), request.capacity(), request.busNumber());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(bus));
    }

    @GetMapping("/buses")
    public ResponseEntity<List<BusResponse>> getAllBuses(
            @RequestParam(required = false) BusStatus status) {
        List<Bus> buses = (status != null)
                ? fleetUseCase.getBusesByStatus(status)
                : fleetUseCase.getAllBuses();
        return ResponseEntity.ok(buses.stream().map(responseMapper::toResponse).toList());
    }

    @GetMapping("/buses/{id}")
    public ResponseEntity<BusResponse> getBus(@PathVariable UUID id) {
        Bus bus = fleetUseCase.getBus(new BusId(id));
        return ResponseEntity.ok(responseMapper.toResponse(bus));
    }

    @PatchMapping("/buses/{id}/maintenance/start")
    public ResponseEntity<BusResponse> sendToMaintenance(@PathVariable UUID id) {
        Bus bus = fleetUseCase.sendBusToMaintenance(new BusId(id));
        return ResponseEntity.ok(responseMapper.toResponse(bus));
    }

    @PatchMapping("/buses/{id}/maintenance/end")
    public ResponseEntity<BusResponse> returnFromMaintenance(@PathVariable UUID id) {
        Bus bus = fleetUseCase.returnBusFromMaintenance(new BusId(id));
        return ResponseEntity.ok(responseMapper.toResponse(bus));
    }

    @DeleteMapping("/buses/{id}")
    public ResponseEntity<Void> retireBus(@PathVariable UUID id) {
        fleetUseCase.retireBus(new BusId(id));
        return ResponseEntity.noContent().build();
    }
}