// domain/model/MaintenanceRecord.java
package com.busgame.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate Root : MaintenanceRecord.
 *
 * Represente un evenement de maintenance — pas juste un statut.
 * Il a son propre cycle de vie : ACTIVE quand le bus est en reparation,
 * COMPLETED quand le bus ressort.
 *
 * Il reference Bus par son BusId uniquement — regle DDD inter-agregats.
 * Bus lui-meme n'a aucune reference vers MaintenanceRecord.
 */
public class MaintenanceRecord {

    private final MaintenanceRecordId id;
    private final BusId busId;
    private final LocalDateTime startTime;      // Heure du jeu au moment de la panne
    private final LocalDateTime scheduledEndTime; // Heure prevue de sortie de maintenance
    private LocalDateTime actualEndTime;         // Heure reelle de sortie (null si encore active)
    private final MaintenanceCause cause;
    private MaintenanceStatus status;

    private MaintenanceRecord(MaintenanceRecordId id, BusId busId,
                              LocalDateTime startTime, LocalDateTime scheduledEndTime,
                              MaintenanceCause cause) {
        this.id = id;
        this.busId = busId;
        this.startTime = startTime;
        this.scheduledEndTime = scheduledEndTime;
        this.cause = cause;
        this.status = MaintenanceStatus.ACTIVE;
        this.actualEndTime = null;
    }

    /**
     * Ouvrir un nouveau dossier de maintenance.
     * Appele par le BusBreakdownHandler (cause = BREAKDOWN)
     * ou par le FleetManagementService (cause = MANUAL).
     */
    public static MaintenanceRecord open(BusId busId, LocalDateTime startTime,
                                         LocalDateTime scheduledEndTime,
                                         MaintenanceCause cause) {
        if (busId == null)
            throw new IllegalArgumentException("BusId est obligatoire.");
        if (startTime == null || scheduledEndTime == null)
            throw new IllegalArgumentException("Les dates sont obligatoires.");
        if (!scheduledEndTime.isAfter(startTime))
            throw new IllegalArgumentException(
                    "La fin prevue doit etre apres le debut.");

        return new MaintenanceRecord(
                new MaintenanceRecordId(UUID.randomUUID()),
                busId, startTime, scheduledEndTime, cause
        );
    }

    public static MaintenanceRecord reconstitute(MaintenanceRecordId id, BusId busId,
                                                 LocalDateTime startTime,
                                                 LocalDateTime scheduledEndTime,
                                                 LocalDateTime actualEndTime,
                                                 MaintenanceCause cause,
                                                 MaintenanceStatus status) {
        MaintenanceRecord record = new MaintenanceRecord(
                id, busId, startTime, scheduledEndTime, cause);
        record.actualEndTime = actualEndTime;
        record.status = status;
        return record;
    }

    /**
     * Verifier si ce dossier est eligible a une cloture automatique.
     * La condition : le dossier est ACTIVE et l'heure du jeu
     * a depasse la fin prevue.
     */
    public boolean isReadyToComplete(LocalDateTime currentGameTime) {
        return this.status == MaintenanceStatus.ACTIVE
                && !currentGameTime.isBefore(this.scheduledEndTime);
    }

    /**
     * Cloturer le dossier de maintenance.
     * On enregistre l'heure reelle de sortie — qui peut differer
     * de l'heure prevue si la simulation etait en pause par exemple.
     */
    public void complete(LocalDateTime actualEndTime) {
        if (this.status != MaintenanceStatus.ACTIVE)
            throw new IllegalStateException(
                    "Seul un dossier ACTIVE peut etre cloture.");
        this.actualEndTime = actualEndTime;
        this.status = MaintenanceStatus.COMPLETED;
    }

    /**
     * Duree reelle de la maintenance en heures.
     * Disponible uniquement apres cloture — utile pour les statistiques.
     */
    public double getActualDurationHours() {
        if (actualEndTime == null)
            throw new IllegalStateException(
                    "La maintenance n'est pas encore terminee.");
        return java.time.Duration.between(startTime, actualEndTime)
                .toMinutes() / 60.0;
    }

    public MaintenanceRecordId getId() { return id; }
    public BusId getBusId() { return busId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getScheduledEndTime() { return scheduledEndTime; }
    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public MaintenanceCause getCause() { return cause; }
    public MaintenanceStatus getStatus() { return status; }
}