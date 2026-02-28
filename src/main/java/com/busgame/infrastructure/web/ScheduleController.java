// infrastructure/web/ScheduleController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.*;
import com.busgame.domain.port.in.ScheduleManagementUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleManagementUseCase scheduleUseCase;
    private final ScheduleResponseMapper responseMapper;

    public ScheduleController(ScheduleManagementUseCase scheduleUseCase,
                              ScheduleResponseMapper responseMapper) {
        this.scheduleUseCase = scheduleUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<ScheduleResponse> planSchedule(
            @RequestBody PlanScheduleRequest request) {
        Schedule schedule = scheduleUseCase.planSchedule(
                new BusId(request.busId()),
                new DriverId(request.driverId()),
                new RouteId(request.routeId()),
                request.startTime(),
                request.endTime()
        );
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMapper.toResponse(schedule));
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        return ResponseEntity.ok(
                scheduleUseCase.getAllSchedules().stream()
                        .map(responseMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(
                responseMapper.toResponse(scheduleUseCase.getSchedule(new ScheduleId(id))));
    }

    @GetMapping("/bus/{busId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesForBus(
            @PathVariable UUID busId) {
        return ResponseEntity.ok(
                scheduleUseCase.getSchedulesForBus(new BusId(busId)).stream()
                        .map(responseMapper::toResponse).toList());
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesForDriver(
            @PathVariable UUID driverId) {
        return ResponseEntity.ok(
                scheduleUseCase.getSchedulesForDriver(new DriverId(driverId)).stream()
                        .map(responseMapper::toResponse).toList());
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ScheduleResponse> cancelSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(
                responseMapper.toResponse(scheduleUseCase.cancelSchedule(new ScheduleId(id))));
    }
}