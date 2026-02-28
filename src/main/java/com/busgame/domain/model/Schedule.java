// domain/model/Schedule.java
package com.busgame.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Aggregate Root : Schedule.
 *
 * Remarque l'absence totale d'objets Bus, Driver ou Route ici —
 * on ne stocke que leurs identifiants. C'est la regle fondamentale
 * des relations inter-agregats en DDD. Schedule est autonome :
 * il sait CE QU'IL PLANIFIE (via les IDs) mais pas CE QUE SONT
 * ces choses (il ne connait pas leur etat interne).
 */
public class Schedule {

    private final ScheduleId id;
    private final BusId busId;
    private final DriverId driverId;
    private final RouteId routeId;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private ScheduleStatus status;
    private TripId tripId;


    private Schedule(ScheduleId id, BusId busId, DriverId driverId, RouteId routeId,
                     LocalDateTime startTime, LocalDateTime endTime) {
        this.id = id;
        this.busId = busId;
        this.driverId = driverId;
        this.routeId = routeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = ScheduleStatus.PLANNED;
    }

    public static Schedule plan(BusId busId, DriverId driverId, RouteId routeId,
                                LocalDateTime startTime, LocalDateTime endTime) {
        // Validation des invariants de base — independamment de tout etat externe
        if (busId == null || driverId == null || routeId == null)
            throw new IllegalArgumentException("Bus, conducteur et parcours sont obligatoires.");
        if (startTime == null || endTime == null)
            throw new IllegalArgumentException("Les heures de debut et de fin sont obligatoires.");
        if (!endTime.isAfter(startTime))
            throw new IllegalArgumentException("L'heure de fin doit etre apres l'heure de debut.");

        return new Schedule(
                new ScheduleId(UUID.randomUUID()),
                busId, driverId, routeId, startTime, endTime
        );
    }

    public static Schedule reconstitute(ScheduleId id, BusId busId, DriverId driverId,
                                        RouteId routeId, LocalDateTime startTime,
                                        LocalDateTime endTime, ScheduleStatus status) {
        Schedule schedule = new Schedule(id, busId, driverId, routeId, startTime, endTime);
        schedule.status = status;
        return schedule;
    }

    /**
     * Verifie si cet horaire chevauche une plage horaire donnee.
     * Deux plages se chevauchent si l'une commence avant que l'autre se termine,
     * ET se termine apres que l'autre commence.
     * C'est la condition de chevauchement classique — memorise-la, tu la reverras souvent.
     */
    public boolean overlapsWith(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return startTime.isBefore(otherEnd) && endTime.isAfter(otherStart);
    }

    /**
     * Calculer la duree du service en heures — utile pour mettre a jour
     * les heures hebdomadaires du conducteur apres le service (Feature 5).
     */
    public double getDurationHours() {
        return java.time.Duration.between(startTime, endTime).toMinutes() / 60.0;
    }

    // Transitions d'etat — seront principalement utilisees par le moteur de simulation
    public void start() {
        if (this.status != ScheduleStatus.PLANNED)
            throw new IllegalStateException("Seul un horaire PLANNED peut demarrer.");
        this.status = ScheduleStatus.IN_PROGRESS;
    }

    public void complete() {
        if (this.status != ScheduleStatus.IN_PROGRESS)
            throw new IllegalStateException("Seul un horaire IN_PROGRESS peut etre complete.");
        this.status = ScheduleStatus.COMPLETED;
    }

    public void cancel() {
        if (this.status == ScheduleStatus.COMPLETED)
            throw new IllegalStateException("Impossible d'annuler un horaire deja complete.");
        this.status = ScheduleStatus.CANCELLED;
    }

    public ScheduleId getId() { return id; }
    public BusId getBusId() { return busId; }
    public DriverId getDriverId() { return driverId; }
    public RouteId getRouteId() { return routeId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public ScheduleStatus getStatus() { return status; }
}