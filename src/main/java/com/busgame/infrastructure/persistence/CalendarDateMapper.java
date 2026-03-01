// infrastructure/persistence/CalendarDateMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.CalendarDate;
import com.busgame.domain.model.CalendarDateId;
import com.busgame.domain.model.ServiceId;
import org.springframework.stereotype.Component;

@Component
public class CalendarDateMapper {

    public CalendarDateJpaEntity toEntity(CalendarDate calendarDate) {
        return new CalendarDateJpaEntity(
                calendarDate.getId().value(),
                calendarDate.getServiceId().value(),
                calendarDate.getDate(),
                calendarDate.getExceptionType()
        );
    }

    public CalendarDate toDomain(CalendarDateJpaEntity entity) {
        return CalendarDate.reconstitute(
                new CalendarDateId(entity.getId()),
                new ServiceId(entity.getServiceId()),
                entity.getDate(),
                entity.getExceptionType()
        );
    }
}