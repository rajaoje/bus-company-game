// domain/model/Calendar.java
package com.busgame.domain.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate Root : Calendar.
 * GTFS : calendar.txt
 *
 * Definit les jours de la semaine actifs pour un service,
 * sur une periode de validite donnee.
 *
 * Exemple : service "semaine scolaire" actif lundi-vendredi
 * du 02/09/2024 au 04/07/2025.
 */
public class Calendar {

    private final ServiceId id;
    private final Set<DayOfWeek> activeDays;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private Calendar(ServiceId id, Set<DayOfWeek> activeDays,
                     LocalDate startDate, LocalDate endDate) {
        this.id         = id;
        this.activeDays = Set.copyOf(activeDays);
        this.startDate  = startDate;
        this.endDate    = endDate;
    }

    public static Calendar create(Set<DayOfWeek> activeDays,
                                  LocalDate startDate,
                                  LocalDate endDate) {
        if (activeDays == null || activeDays.isEmpty())
            throw new IllegalArgumentException(
                    "Au moins un jour actif est requis.");
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException(
                    "Les dates de debut et de fin sont obligatoires.");
        if (!endDate.isAfter(startDate))
            throw new IllegalArgumentException(
                    "La date de fin doit etre apres la date de debut.");

        return new Calendar(
                new ServiceId(UUID.randomUUID()),
                activeDays, startDate, endDate);
    }

    public static Calendar reconstitute(ServiceId id,
                                        Set<DayOfWeek> activeDays,
                                        LocalDate startDate,
                                        LocalDate endDate) {
        return new Calendar(id, activeDays, startDate, endDate);
    }

    /**
     * Ce service est-il actif a une date donnee ?
     * Verifie : date dans la periode ET jour de semaine actif.
     * Les exceptions (CalendarDate) sont evaluees separement.
     */
    public boolean isActiveOn(LocalDate date) {
        if (date.isBefore(startDate) || date.isAfter(endDate))
            return false;
        return activeDays.contains(date.getDayOfWeek());
    }

    public ServiceId getId()            { return id; }
    public Set<DayOfWeek> getActiveDays(){ return activeDays; }
    public LocalDate getStartDate()     { return startDate; }
    public LocalDate getEndDate()       { return endDate; }
}