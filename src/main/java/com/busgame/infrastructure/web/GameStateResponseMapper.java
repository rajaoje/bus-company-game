// infrastructure/web/GameStateResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.GameState;
import org.springframework.stereotype.Component;

@Component
public class GameStateResponseMapper {
    public GameStateResponse toResponse(GameState gameState) {
        String description = gameState.isRunning()
                ? "Simulation en cours — " + gameState.getSpeedMultiplier() +
                " minute(s) de jeu par tick"
                : "Simulation en pause";

        return new GameStateResponse(
                gameState.getCurrentGameTime(),
                gameState.getStatus(),
                gameState.getSpeedMultiplier(),
                description
        );
    }
}