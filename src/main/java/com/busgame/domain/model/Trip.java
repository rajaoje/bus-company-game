// domain/model/Trip.java
package com.busgame.domain.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

/**
 * GTFS : trips.txt
 *
 * Un Trip est un passage concret d'un bus sur une Route
 * a une heure donnee. La Route definit QUELS arrets sont desservis,
 * le Trip definit A QUELLE HEURE ils le sont.
 *
 * C'est le nouvel agregat central du reseau — il remplace la relation
 * directe Route → Stop de l'ancien modele.
 */
public class Trip {

    private final TripId id;
    private final RouteId routeId;
    private DirectionId directionId;  // GTFS : direction_id
    private String headsign;          // GTFS : trip_headsign — ex: "Gare Nord"
    private List<StopTime> stopTimes;
    private final ServiceId serviceId;

    private static final double AVERAGE_SPEED_KMH = 25.0;

    private Trip(TripId id, RouteId routeId, ServiceId serviceId,
                 DirectionId directionId, String headsign) {
        this.id          = id;
        this.routeId     = routeId;
        this.serviceId   = serviceId;
        this.directionId = directionId;
        this.headsign    = headsign;
        this.stopTimes   = new ArrayList<>();
    }

    public static Trip create(RouteId routeId, ServiceId serviceId,
                              DirectionId directionId, String headsign) {
        if (routeId == null)
            throw new IllegalArgumentException("RouteId est obligatoire.");
        if (serviceId == null)
            throw new IllegalArgumentException("ServiceId est obligatoire.");
        if (directionId == null)
            throw new IllegalArgumentException("DirectionId est obligatoire.");
        if (headsign == null || headsign.isBlank())
            throw new IllegalArgumentException("Le headsign est obligatoire.");

        return new Trip(new TripId(UUID.randomUUID()),
                routeId, serviceId, directionId, headsign);
    }

    public static Trip reconstitute(TripId id, RouteId routeId,
                                    ServiceId serviceId,
                                    DirectionId directionId, String headsign,
                                    List<StopTime> stopTimes) {
        Trip trip = new Trip(id, routeId, serviceId, directionId, headsign);
        trip.stopTimes.addAll(stopTimes);
        return trip;
    }

    /**
     * Ajouter un arret avec ses horaires au trip.
     * Les horaires doivent etre croissants — on ne peut pas arriver
     * a un arret avant d'etre parti du precedent.
     */
    public StopTime addStopTime(StopId stopId,
                                LocalTime arrivalTime,
                                LocalTime departureTime,
                                Distance distanceFromPrevious) {
        if (stopId == null)
            throw new IllegalArgumentException("StopId est obligatoire.");

        // Verifier la coherence des horaires
        if (departureTime.isBefore(arrivalTime))
            throw new IllegalArgumentException(
                    "L'heure de depart ne peut pas preceder l'heure d'arrivee.");

        // Verifier que l'heure d'arrivee est apres le depart du precedent
        if (!stopTimes.isEmpty()) {
            LocalTime lastDeparture = stopTimes
                    .get(stopTimes.size() - 1)
                    .getDepartureTime();
            if (arrivalTime.isBefore(lastDeparture))
                throw new IllegalArgumentException(
                        "L'heure d'arrivee doit etre apres le depart de l'arret precedent.");
        }

        StopTime stopTime = new StopTime(
                new StopTimeId(UUID.randomUUID()),
                stopId,
                stopTimes.size() + 1,
                arrivalTime,
                departureTime,
                distanceFromPrevious
        );
        stopTimes.add(stopTime);
        return stopTime;
    }

    /**
     * Supprimer un stopTime du trip.
     * Reordonne automatiquement la sequence apres suppression.
     */
    public void removeStopTime(StopTimeId stopTimeId) {
        if (stopTimes.size() <= 2)
            throw new IllegalStateException(
                    "Un trip doit avoir au minimum deux arrets.");

        boolean removed = stopTimes.removeIf(
                st -> st.getId().equals(stopTimeId));
        if (!removed)
            throw new IllegalArgumentException(
                    "StopTime introuvable dans ce trip.");

        // Reordonner la sequence
        for (int i = 0; i < stopTimes.size(); i++) {
            stopTimes.get(i).setStopSequence(i + 1);
        }
    }

    /**
     * Distance totale du trip — somme des distances entre arrets.
     */
    public Distance getTotalDistance() {
        return stopTimes.stream()
                .map(StopTime::getDistanceFromPrevious)
                .reduce(Distance.zero(), Distance::add);
    }

    /**
     * Duree estimee basee sur la vitesse moyenne.
     * En pratique, la duree reelle est la difference entre
     * le premier arrivalTime et le dernier arrivalTime.
     */
    public double getEstimatedDurationMinutes() {
        if (stopTimes.size() < 2) return 0;

        LocalTime first = stopTimes.get(0).getArrivalTime();
        LocalTime last  = stopTimes.get(stopTimes.size() - 1)
                .getArrivalTime();
        return java.time.Duration.between(first, last).toMinutes();
    }

    public TripId getId()           { return id; }
    public RouteId getRouteId()     { return routeId; }
    public DirectionId getDirectionId() { return directionId; }
    public String getHeadsign()     { return headsign; }
    public ServiceId getServiceId() { return serviceId; }
    public List<StopTime> getStopTimes() {
        return Collections.unmodifiableList(
                stopTimes.stream()
                        .sorted(Comparator.comparingInt(StopTime::getStopSequence))
                        .toList()
        );
    }
}