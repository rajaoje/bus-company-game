// infrastructure/persistence/GameStateJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.SimulationStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "game_state")
public class GameStateJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "current_game_time", nullable = false)
    private LocalDateTime currentGameTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SimulationStatus status;

    @Column(name = "speed_multiplier", nullable = false)
    private int speedMultiplier;

    @Column(name = "tick_interval_seconds", nullable = false)
    private int tickIntervalSeconds;

    protected GameStateJpaEntity() {}

    public GameStateJpaEntity(UUID id, LocalDateTime currentGameTime,
                              SimulationStatus status, int speedMultiplier,
                              int tickIntervalSeconds) {
        this.id = id;
        this.currentGameTime = currentGameTime;
        this.status = status;
        this.speedMultiplier = speedMultiplier;
        this.tickIntervalSeconds = tickIntervalSeconds;
    }

    public UUID getId() { return id; }
    public LocalDateTime getCurrentGameTime() { return currentGameTime; }
    public SimulationStatus getStatus() { return status; }
    public int getSpeedMultiplier() { return speedMultiplier; }
    public int getTickIntervalSeconds() { return tickIntervalSeconds; }
}