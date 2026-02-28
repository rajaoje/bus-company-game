// domain/port/out/ScheduleRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepository {
    Schedule save(Schedule schedule);
    Optional<Schedule> findById(ScheduleId id);
    List<Schedule> findAll();
    List<Schedule> findByBusId(BusId busId);
    List<Schedule> findByDriverId(DriverId driverId);

    /**
     * Ces deux methodes sont le coeur de la verification de chevauchement.
     * Elles cherchent s'il existe un horaire actif (PLANNED ou IN_PROGRESS)
     * pour un bus ou un conducteur donne, qui chevauche la plage horaire donnee.
     * On les definit ici dans le port pour que le Use Case puisse les appeler
     * sans se soucier de comment la requete SQL est construite.
     */
    List<Schedule> findActiveSchedulesForBusInPeriod(BusId busId,
                                                     LocalDateTime start,
                                                     LocalDateTime end);
    List<Schedule> findActiveSchedulesForDriverInPeriod(DriverId driverId,
                                                        LocalDateTime start,
                                                        LocalDateTime end);
    // A ajouter dans domain/port/out/ScheduleRepository.java

    /**
     * Trouve les horaires PLANNED dont l'heure de debut
     * est inferieure ou egale a l'heure du jeu courante.
     * Ce sont les horaires qui auraient du demarrer.
     */
    List<Schedule> findSchedulesToStart(LocalDateTime gameTime);

    /**
     * Trouve les horaires IN_PROGRESS dont l'heure de fin
     * est inferieure ou egale a l'heure du jeu courante.
     * Ce sont les horaires qui auraient du se terminer.
     */
    List<Schedule> findSchedulesToComplete(LocalDateTime gameTime);

    /**
     * Trouve tous les horaires avec un statut donne.
     * Utilise pour trouver les horaires actifs lors des evenements aleatoires.
     */
    List<Schedule> findByStatus(ScheduleStatus status);
}