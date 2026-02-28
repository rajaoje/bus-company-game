// infrastructure/persistence/GameStateRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.GameState;
import com.busgame.domain.port.out.GameStateRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GameStateRepositoryAdapter implements GameStateRepository {

    private final SpringDataGameStateRepository springDataRepo;
    private final GameStateMapper mapper;

    public GameStateRepositoryAdapter(SpringDataGameStateRepository springDataRepo,
                                      GameStateMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public GameState save(GameState gameState) {
        return mapper.toDomain(springDataRepo.save(mapper.toEntity(gameState)));
    }

    @Override
    public Optional<GameState> find() {
        return springDataRepo.findById(GameState.GAME_STATE_ID).map(mapper::toDomain);
    }
}