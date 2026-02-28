// infrastructure/persistence/SpringDataGameStateRepository.java
package com.busgame.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataGameStateRepository
        extends JpaRepository<GameStateJpaEntity, UUID> {}