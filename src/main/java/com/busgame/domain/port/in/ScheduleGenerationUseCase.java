// domain/port/in/ScheduleGenerationUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.Schedule;
import java.time.LocalDate;
import java.util.List;

public interface ScheduleGenerationUseCase {
    /**
     * Genere tous les Schedules pour une date donnee
     * a partir des Trips actifs ce jour-la.
     * Retourne les schedules crees.
     */
    List<Schedule> generateSchedulesForDate(LocalDate date);
}