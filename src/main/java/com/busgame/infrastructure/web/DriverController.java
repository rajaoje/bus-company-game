// infrastructure/web/DriverController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverId;
import com.busgame.domain.model.DriverStatus;
import com.busgame.domain.port.in.DriverManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/drivers")
public class DriverController {

    private final DriverManagementUseCase driverUseCase;
    private final DriverResponseMapper responseMapper;

    public DriverController(DriverManagementUseCase driverUseCase,
                            DriverResponseMapper responseMapper) {
        this.driverUseCase = driverUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<DriverResponse> hireDriver(@RequestBody HireDriverRequest request) {
        Driver driver = driverUseCase.hireDriver(
                request.firstName(), request.lastName(), request.email()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMapper.toResponse(driver));
    }

    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers(
            @RequestParam(required = false) DriverStatus status) {
        List<Driver> drivers = (status != null)
                ? driverUseCase.getDriversByStatus(status)
                : driverUseCase.getAllDrivers();
        return ResponseEntity.ok(drivers.stream().map(responseMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriver(@PathVariable UUID id) {
        return ResponseEntity.ok(responseMapper.toResponse(driverUseCase.getDriver(new DriverId(id))));
    }

    @PatchMapping("/{id}/leave/start")
    public ResponseEntity<DriverResponse> sendOnLeave(@PathVariable UUID id) {
        return ResponseEntity.ok(responseMapper.toResponse(
                driverUseCase.sendDriverOnLeave(new DriverId(id))));
    }

    @PatchMapping("/{id}/leave/end")
    public ResponseEntity<DriverResponse> returnFromLeave(@PathVariable UUID id) {
        return ResponseEntity.ok(responseMapper.toResponse(
                driverUseCase.returnDriverFromLeave(new DriverId(id))));
    }

    @PatchMapping("/{id}/suspend")
    public ResponseEntity<DriverResponse> suspend(@PathVariable UUID id) {
        return ResponseEntity.ok(responseMapper.toResponse(
                driverUseCase.suspendDriver(new DriverId(id))));
    }

    @PatchMapping("/{id}/reinstate")
    public ResponseEntity<DriverResponse> reinstate(@PathVariable UUID id) {
        return ResponseEntity.ok(responseMapper.toResponse(
                driverUseCase.reinstateDriver(new DriverId(id))));
    }
}