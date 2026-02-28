// infrastructure/persistence/GameStateMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.GameState;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class GameStateMapper {

    public GameStateJpaEntity toEntity(GameState gameState) {
        return new GameStateJpaEntity(
                GameState.GAME_STATE_ID,
                gameState.getCurrentGameTime(),
                gameState.getStatus(),
                gameState.getSpeedMultiplier(),
                gameState.getTickIntervalSeconds()
        );
    }

    public GameState toDomain(GameStateJpaEntity entity) {
        return GameState.reconstitute(
                entity.getCurrentGameTime(),
                entity.getStatus(),
                entity.getSpeedMultiplier(),
                entity.getTickIntervalSeconds()
        );
    }
}