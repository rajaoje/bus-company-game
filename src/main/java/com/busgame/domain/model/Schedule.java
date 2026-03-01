// domain/model/Schedule.java
package com.busgame.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate Root : Schedule.
 *
 * Un horaire relie un Bus, un Driver, et (apres Niveau 2 GTFS)
 * un Trip concret — pas seulement une Route abstraite.
 *
 * Regles de design inter-agregats :
 * On ne stocke que les identifiants (BusId, DriverId, RouteId, TripId) —
 * jamais les objets complets. Schedule sait CE QU'IL PLANIFIE
 * mais pas CE QUE SONT ces entites.
 *
 * Evolution GTFS Niveau 2 :
 * - tripId ajouté — le Trip est le parcours concret avec ses horaires
 * - routeId conservé temporairement pour rétrocompatibilité
 *   (sera supprimé quand le front-end sera migré vers tripId)
 */
public class Schedule {

    private final ScheduleId id;
    private final BusId busId;
    private final DriverId driverId;
    private final RouteId routeId;   // Conservé pour rétrocompatibilité
    private final TripId tripId;     // GTFS Niveau 2 — le Trip concret
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private ScheduleStatus status;

    private Schedule(ScheduleId id, BusId busId, DriverId driverId,RouteId routeId,
                     TripId tripId,
                     LocalDateTime startTime, LocalDateTime endTime) {
        this.id        = id;
        this.busId     = busId;
        this.driverId  = driverId;
        this.routeId   = routeId;
        this.tripId    = tripId;
        this.startTime = startTime;
        this.endTime   = endTime;
        this.status    = ScheduleStatus.PLANNED;
    }

    /**
     * Planifier un nouveau service.
     *
     * Apres Niveau 2 GTFS : on passe tripId en plus de routeId.
     * Le Trip porte les horaires theoriques (StopTime) qui permettront
     * de calculer le kilometrage reel dans ScheduleCompletedHandler.
     */
    public static Schedule plan(BusId busId, DriverId driverId,
                                RouteId routeId, TripId tripId,
                                LocalDateTime startTime,
                                LocalDateTime endTime) {
        if (busId == null || driverId == null || routeId == null)
            throw new IllegalArgumentException(
                    "Bus, conducteur et parcours sont obligatoires.");
        if (tripId == null)
            throw new IllegalArgumentException(
                    "Le trip est obligatoire depuis le Niveau 2 GTFS.");
        if (startTime == null || endTime == null)
            throw new IllegalArgumentException(
                    "Les heures de debut et de fin sont obligatoires.");
        if (!endTime.isAfter(startTime))
            throw new IllegalArgumentException(
                    "L'heure de fin doit etre apres l'heure de debut.");

        return new Schedule(
                new ScheduleId(UUID.randomUUID()),
                busId, driverId, routeId, tripId,
                startTime, endTime
        );
    }

    /**
     * Reconstitution depuis la base de donnees.
     * Accepte tripId nullable pour assurer la migration progressive —
     * les anciens horaires en base n'ont pas encore de tripId.
     */
    public static Schedule reconstitute(ScheduleId id,
                                        BusId busId,
                                        DriverId driverId,
                                        RouteId routeId,
                                        TripId tripId,        // nullable pre-migration
                                        LocalDateTime startTime,
                                        LocalDateTime endTime,
                                        ScheduleStatus status) {
        Schedule schedule = new Schedule(
                id, busId, driverId, routeId, tripId,
                startTime, endTime
        );
        schedule.status = status;
        return schedule;
    }

    // ── Transitions d'état ───────────────────────────────────────────

    /**
     * Demarrer le service — transition PLANNED → IN_PROGRESS.
     * Appele par ScheduleStartedHandler quand la simulation
     * atteint l'heure de debut.
     */
    public void start() {
        if (this.status != ScheduleStatus.PLANNED)
            throw new IllegalStateException(
                    "Seul un horaire PLANNED peut demarrer. " +
                            "Statut actuel : " + this.status);
        this.status = ScheduleStatus.IN_PROGRESS;
    }

    /**
     * Terminer le service — transition IN_PROGRESS → COMPLETED.
     * Appele par ScheduleCompletedHandler quand la simulation
     * atteint l'heure de fin.
     */
    public void complete() {
        if (this.status != ScheduleStatus.IN_PROGRESS)
            throw new IllegalStateException(
                    "Seul un horaire IN_PROGRESS peut etre complete. " +
                            "Statut actuel : " + this.status);
        this.status = ScheduleStatus.COMPLETED;
    }

    /**
     * Annuler l'horaire — possible depuis PLANNED ou IN_PROGRESS.
     * Appele manuellement par le joueur ou par BusBreakdownHandler
     * en cas de panne.
     */
    public void cancel() {
        if (this.status == ScheduleStatus.COMPLETED)
            throw new IllegalStateException(
                    "Impossible d'annuler un horaire deja complete.");
        if (this.status == ScheduleStatus.CANCELLED)
            throw new IllegalStateException(
                    "L'horaire est deja annule.");
        this.status = ScheduleStatus.CANCELLED;
    }

    // ── Logique métier ────────────────────────────────────────────────

    /**
     * Verifie si cet horaire chevauche une plage horaire donnee.
     *
     * Formule classique de detection de chevauchement entre deux intervalles :
     * [A, B] chevauche [C, D] si A < D ET B > C.
     *
     * Utilisee par ScheduleManagementService pour empecher les
     * conflits de planification (meme bus ou meme conducteur
     * sur deux horaires simultanes).
     */
    public boolean overlapsWith(LocalDateTime otherStart,
                                LocalDateTime otherEnd) {
        return startTime.isBefore(otherEnd)
                && endTime.isAfter(otherStart);
    }

    /**
     * Duree du service en heures.
     *
     * Utilisee par :
     * - ScheduleManagementService pour verifier les heures
     *   hebdomadaires disponibles du conducteur avant planification.
     * - ScheduleCompletedHandler pour crediter les heures
     *   du conducteur apres le service.
     */
    public double getDurationHours() {
        return java.time.Duration
                .between(startTime, endTime)
                .toMinutes() / 60.0;
    }

    // ── Getters ───────────────────────────────────────────────────────

    public ScheduleId getId()             { return id; }
    public BusId getBusId()               { return busId; }
    public DriverId getDriverId()         { return driverId; }
    public RouteId getRouteId()           { return routeId; }

    /**
     * Retourne le TripId associe a cet horaire.
     *
     * Peut etre null pour les horaires crees avant le Niveau 2 GTFS
     * (periode de migration). Les handlers verifient ce cas
     * avant d'utiliser le Trip pour calculer le kilometrage.
     */
    public TripId getTripId()             { return tripId; }

    public LocalDateTime getStartTime()   { return startTime; }
    public LocalDateTime getEndTime()     { return endTime; }
    public ScheduleStatus getStatus()     { return status; }
}