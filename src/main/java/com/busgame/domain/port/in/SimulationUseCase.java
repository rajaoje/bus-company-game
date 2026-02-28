// domain/port/in/SimulationUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.GameState;

public interface SimulationUseCase {
    GameState startSimulation(java.time.LocalDateTime startTime);
    GameState getGameState();
    GameState pauseSimulation();
    GameState resumeSimulation();
    GameState setSpeed(int multiplier);
    GameState processTick(); // Peut aussi etre appele manuellement
}