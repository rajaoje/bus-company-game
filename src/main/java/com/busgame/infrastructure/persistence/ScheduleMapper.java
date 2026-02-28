// infrastructure/persistence/ScheduleMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class ScheduleMapper {

    public ScheduleJpaEntity toEntity(Schedule schedule) {
        return new ScheduleJpaEntity(
                schedule.getId().value(),
                schedule.getBusId().value(),
                schedule.getDriverId().value(),
                schedule.getRouteId().value(),
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }

    public Schedule toDomain(ScheduleJpaEntity entity) {
        return Schedule.reconstitute(
                new ScheduleId(entity.getId()),
                new BusId(entity.getBusId()),
                new DriverId(entity.getDriverId()),
                new RouteId(entity.getRouteId()),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus()
        );
    }
}