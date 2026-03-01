// infrastructure/persistence/ScheduleMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import org.springframework.stereotype.Component;

// infrastructure/persistence/ScheduleMapper.java
@Component
public class ScheduleMapper {

    public ScheduleJpaEntity toEntity(Schedule schedule) {
        return new ScheduleJpaEntity(
                schedule.getId().value(),
                schedule.getBusId().value(),
                schedule.getDriverId().value(),
                schedule.getRouteId().value(),
                schedule.getTripId() != null
                        ? schedule.getTripId().value() : null,
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus()
        );
    }

    public Schedule toDomain(ScheduleJpaEntity entity) {
        TripId tripId = entity.getTripId() != null
                ? new TripId(entity.getTripId()) : null;

        return Schedule.reconstitute(
                new ScheduleId(entity.getId()),
                new BusId(entity.getBusId()),
                new DriverId(entity.getDriverId()),
                new RouteId(entity.getRouteId()),
                tripId,
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getStatus()
        );
    }
}