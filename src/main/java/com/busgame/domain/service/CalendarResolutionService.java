// domain/service/CalendarResolutionService.java
package com.busgame.domain.service;

import com.busgame.domain.model.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Domain Service pur — pas d'annotation Spring.
 *
 * Determine si un service est actif a une date donnee,
 * en tenant compte du Calendar de base ET des exceptions CalendarDate.
 *
 * Ordre de priorite GTFS :
 * 1. Si une CalendarDate REMOVED existe pour cette date → inactif
 * 2. Si une CalendarDate ADDED existe pour cette date → actif
 * 3. Sinon, se fier au Calendar de base (isActiveOn)
 */
public class CalendarResolutionService {

    public boolean isServiceActiveOn(Calendar calendar,
                                     List<CalendarDate> exceptions,
                                     LocalDate date) {
        for (CalendarDate exception : exceptions) {
            if (exception.getDate().equals(date)) {
                return exception.getExceptionType()
                        == CalendarDateException.ADDED;
            }
        }
        return calendar.isActiveOn(date);
    }
}