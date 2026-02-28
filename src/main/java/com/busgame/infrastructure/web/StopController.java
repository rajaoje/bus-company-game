// infrastructure/web/StopController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.StopId;
import com.busgame.domain.port.in.StopManagementUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/stops")
public class StopController {

    private final StopManagementUseCase stopUseCase;
    private final StopResponseMapper responseMapper;

    public StopController(StopManagementUseCase stopUseCase,
                          StopResponseMapper responseMapper) {
        this.stopUseCase    = stopUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public ResponseEntity<StopResponse> createStop(
            @Valid @RequestBody CreateStopRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(responseMapper.toResponse(
                        stopUseCase.createStop(
                                request.name(),
                                request.latitude(),
                                request.longitude()
                        )));
    }

    @GetMapping
    public ResponseEntity<List<StopResponse>> getAllStops() {
        return ResponseEntity.ok(
                stopUseCase.getAllStops()
                        .stream()
                        .map(responseMapper::toResponse)
                        .toList()
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStop(@PathVariable UUID id) {
        stopUseCase.deleteStop(new StopId(id));
        return ResponseEntity.noContent().build();
    }
}