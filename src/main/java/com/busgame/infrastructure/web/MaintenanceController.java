// infrastructure/web/MaintenanceController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.BusId;
import com.busgame.domain.port.out.MaintenanceRecordRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Expose l'historique des maintenances en lecture seule.
 * La creation de maintenances passe par FleetManagementUseCase
 * (maintenance manuelle) ou par le moteur de simulation (pannes).
 */
@RestController
@RequestMapping("/api/v1/maintenance")
public class MaintenanceController {

    private final MaintenanceRecordRepository maintenanceRecordRepository;
    private final MaintenanceRecordResponseMapper responseMapper;

    public MaintenanceController(
            MaintenanceRecordRepository maintenanceRecordRepository,
            MaintenanceRecordResponseMapper responseMapper) {
        this.maintenanceRecordRepository = maintenanceRecordRepository;
        this.responseMapper = responseMapper;
    }

    // Tout l'historique de maintenance d'un bus
    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<MaintenanceRecordResponse>> getMaintenanceHistory(
            @PathVariable UUID busId) {
        return ResponseEntity.ok(
                maintenanceRecordRepository.findByBusId(new BusId(busId))
                        .stream().map(responseMapper::toResponse).toList()
        );
    }

    // La maintenance active d'un bus (s'il en a une)
    @GetMapping("/bus/{busId}/active")
    public ResponseEntity<MaintenanceRecordResponse> getActiveMaintenance(
            @PathVariable UUID busId) {
        return maintenanceRecordRepository.findActiveByBusId(new BusId(busId))
                .map(record -> ResponseEntity.ok(responseMapper.toResponse(record)))
                .orElse(ResponseEntity.notFound().build());
    }
}