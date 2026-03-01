// domain/model/CalendarDate.java
package com.busgame.domain.model;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Aggregate Root : CalendarDate.
 * GTFS : calendar_dates.txt
 *
 * Exception ponctuelle a un Calendar.
 * Exemple : le 25 decembre, supprimer le service "semaine".
 * Exemple : le 14 juillet, ajouter un service special.
 */
public class CalendarDate {

    private final CalendarDateId id;
    private final ServiceId serviceId;
    private final LocalDate date;
    private final CalendarDateException exceptionType;

    private CalendarDate(CalendarDateId id, ServiceId serviceId,
                         LocalDate date,
                         CalendarDateException exceptionType) {
        this.id            = id;
        this.serviceId     = serviceId;
        this.date          = date;
        this.exceptionType = exceptionType;
    }

    public static CalendarDate create(ServiceId serviceId,
                                      LocalDate date,
                                      CalendarDateException exceptionType) {
        if (serviceId == null)
            throw new IllegalArgumentException("ServiceId est obligatoire.");
        if (date == null)
            throw new IllegalArgumentException("La date est obligatoire.");
        if (exceptionType == null)
            throw new IllegalArgumentException(
                    "Le type d'exception est obligatoire.");

        return new CalendarDate(
                new CalendarDateId(UUID.randomUUID()),
                serviceId, date, exceptionType);
    }

    public static CalendarDate reconstitute(CalendarDateId id,
                                            ServiceId serviceId,
                                            LocalDate date,
                                            CalendarDateException exceptionType) {
        return new CalendarDate(id, serviceId, date, exceptionType);
    }

    public CalendarDateId getId()                   { return id; }
    public ServiceId getServiceId()                 { return serviceId; }
    public LocalDate getDate()                      { return date; }
    public CalendarDateException getExceptionType() { return exceptionType; }
}