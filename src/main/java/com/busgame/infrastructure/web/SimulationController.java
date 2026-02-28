// infrastructure/web/SimulationController.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.GameState;
import com.busgame.domain.port.in.SimulationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/simulation")
public class SimulationController {

    private final SimulationUseCase simulationUseCase;
    private final GameStateResponseMapper responseMapper;

    public SimulationController(SimulationUseCase simulationUseCase,
                                GameStateResponseMapper responseMapper) {
        this.simulationUseCase = simulationUseCase;
        this.responseMapper = responseMapper;
    }

    @PostMapping("/start")
    public ResponseEntity<GameStateResponse> start(
            @RequestBody StartSimulationRequest request) {
        GameState state = simulationUseCase.startSimulation(request.startTime());
        return ResponseEntity.ok(responseMapper.toResponse(state));
    }

    @GetMapping("/state")
    public ResponseEntity<GameStateResponse> getState() {
        return ResponseEntity.ok(responseMapper.toResponse(simulationUseCase.getGameState()));
    }

    @PostMapping("/pause")
    public ResponseEntity<GameStateResponse> pause() {
        return ResponseEntity.ok(responseMapper.toResponse(simulationUseCase.pauseSimulation()));
    }

    @PostMapping("/resume")
    public ResponseEntity<GameStateResponse> resume() {
        return ResponseEntity.ok(responseMapper.toResponse(simulationUseCase.resumeSimulation()));
    }

    @PostMapping("/tick")
    public ResponseEntity<GameStateResponse> tick() {
        // Endpoint manuel — utile pour deboguer ou mode tour par tour
        return ResponseEntity.ok(responseMapper.toResponse(simulationUseCase.processTick()));
    }

    @PatchMapping("/speed")
    public ResponseEntity<GameStateResponse> setSpeed(@RequestBody SetSpeedRequest request) {
        return ResponseEntity.ok(
                responseMapper.toResponse(simulationUseCase.setSpeed(request.multiplier())));
    }
}