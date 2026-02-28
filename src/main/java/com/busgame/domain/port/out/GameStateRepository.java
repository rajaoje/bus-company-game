// domain/port/out/GameStateRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.GameState;
import java.util.Optional;

public interface GameStateRepository {
    GameState save(GameState gameState);
    Optional<GameState> find(); // Il n'y a qu'un seul GameState
}