// infrastructure/web/ScheduleResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Schedule;
import org.springframework.stereotype.Component;

// infrastructure/web/ScheduleResponseMapper.java
@Component
public class ScheduleResponseMapper {
    public ScheduleResponse toResponse(Schedule schedule) {
        return new ScheduleResponse(
                schedule.getId().value(),
                schedule.getBusId().value(),
                schedule.getDriverId().value(),
                schedule.getRouteId().value(),
                schedule.getTripId() != null
                        ? schedule.getTripId().value() : null,
                schedule.getStartTime(),
                schedule.getEndTime(),
                schedule.getStatus(),
                schedule.getDurationHours()
        );
    }
}