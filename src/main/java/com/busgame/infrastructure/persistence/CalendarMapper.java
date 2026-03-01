package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Calendar;
import com.busgame.domain.model.ServiceId;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.util.HashSet;
import java.util.Set;

// infrastructure/persistence/CalendarMapper.java
@Component
public class CalendarMapper {

    public CalendarJpaEntity toEntity(Calendar calendar) {
        Set<DayOfWeek> days = calendar.getActiveDays();
        return new CalendarJpaEntity(
                calendar.getId().value(),
                days.contains(DayOfWeek.MONDAY),
                days.contains(DayOfWeek.TUESDAY),
                days.contains(DayOfWeek.WEDNESDAY),
                days.contains(DayOfWeek.THURSDAY),
                days.contains(DayOfWeek.FRIDAY),
                days.contains(DayOfWeek.SATURDAY),
                days.contains(DayOfWeek.SUNDAY),
                calendar.getStartDate(),
                calendar.getEndDate()
        );
    }

    public Calendar toDomain(CalendarJpaEntity entity) {
        Set<DayOfWeek> days = new HashSet<>();
        if (entity.isMonday())    days.add(DayOfWeek.MONDAY);
        if (entity.isTuesday())   days.add(DayOfWeek.TUESDAY);
        if (entity.isWednesday()) days.add(DayOfWeek.WEDNESDAY);
        if (entity.isThursday())  days.add(DayOfWeek.THURSDAY);
        if (entity.isFriday())    days.add(DayOfWeek.FRIDAY);
        if (entity.isSaturday())  days.add(DayOfWeek.SATURDAY);
        if (entity.isSunday())    days.add(DayOfWeek.SUNDAY);

        return Calendar.reconstitute(
                new ServiceId(entity.getId()),
                days,
                entity.getStartDate(),
                entity.getEndDate()
        );
    }
}