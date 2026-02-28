// domain/model/GameState.java
package com.busgame.domain.model;

import java.time.LocalDateTime;

/**
 * Aggregate Root qui represente l'etat courant de la simulation.
 * Il n'en existe qu'une seule instance en base de donnees.
 * C'est lui qui repond a la question : "On est quand dans le jeu ?"
 */
public class GameState {

    // ID fixe — il n'y a toujours qu'un seul GameState dans le jeu
    public static final java.util.UUID GAME_STATE_ID =
            java.util.UUID.fromString("00000000-0000-0000-0000-000000000001");

    private LocalDateTime currentGameTime;  // L'heure actuelle dans le jeu
    private SimulationStatus status;
    private int speedMultiplier; // x1, x5, x10 — combien de minutes de jeu par tick
    private int tickIntervalSeconds; // Frequence des ticks en temps reel

    private GameState(LocalDateTime currentGameTime, int speedMultiplier,
                      int tickIntervalSeconds) {
        this.currentGameTime = currentGameTime;
        this.status = SimulationStatus.PAUSED;
        this.speedMultiplier = speedMultiplier;
        this.tickIntervalSeconds = tickIntervalSeconds;
    }

    /**
     * Creer une nouvelle partie — le jeu commence a une date donnee,
     * par defaut en pause. Le joueur demarre quand il est pret.
     */
    public static GameState initialize(LocalDateTime startTime) {
        if (startTime == null)
            throw new IllegalArgumentException("La date de debut est obligatoire.");
        return new GameState(startTime, 1, 5);
    }

    public static GameState reconstitute(LocalDateTime currentGameTime,
                                         SimulationStatus status,
                                         int speedMultiplier,
                                         int tickIntervalSeconds) {
        GameState state = new GameState(currentGameTime, speedMultiplier, tickIntervalSeconds);
        state.status = status;
        return state;
    }

    /**
     * Avancer le temps du jeu d'un tick.
     * Un tick fait avancer le temps de (speedMultiplier) minutes.
     * Exemple : speedMultiplier=5 → chaque tick avance le jeu de 5 minutes.
     */
    public LocalDateTime tick() {
        if (status != SimulationStatus.RUNNING)
            throw new IllegalStateException("La simulation n'est pas en cours.");
        currentGameTime = currentGameTime.plusMinutes(speedMultiplier);
        return currentGameTime;
    }

    public void start() {
        if (status == SimulationStatus.RUNNING)
            throw new IllegalStateException("La simulation est deja en cours.");
        status = SimulationStatus.RUNNING;
    }

    public void pause() {
        if (status != SimulationStatus.RUNNING)
            throw new IllegalStateException("La simulation n'est pas en cours.");
        status = SimulationStatus.PAUSED;
    }

    public void resume() {
        if (status != SimulationStatus.PAUSED)
            throw new IllegalStateException("La simulation n'est pas en pause.");
        status = SimulationStatus.RUNNING;
    }

    public void setSpeedMultiplier(int multiplier) {
        if (multiplier < 1 || multiplier > 60)
            throw new IllegalArgumentException("Le multiplicateur doit etre entre 1 et 60.");
        this.speedMultiplier = multiplier;
    }

    public LocalDateTime getCurrentGameTime() { return currentGameTime; }
    public SimulationStatus getStatus() { return status; }
    public int getSpeedMultiplier() { return speedMultiplier; }
    public int getTickIntervalSeconds() { return tickIntervalSeconds; }
    public boolean isRunning() { return status == SimulationStatus.RUNNING; }
}