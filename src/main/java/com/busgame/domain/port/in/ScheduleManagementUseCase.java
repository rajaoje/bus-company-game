// domain/port/in/ScheduleManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;

// domain/port/in/ScheduleManagementUseCase.java
public interface ScheduleManagementUseCase {
    Schedule planSchedule(BusId busId, DriverId driverId,
                          RouteId routeId, TripId tripId,
                          LocalDateTime startTime, LocalDateTime endTime);
    Schedule getSchedule(ScheduleId id);
    List<Schedule> getAllSchedules();
    List<Schedule> getSchedulesForBus(BusId busId);
    List<Schedule> getSchedulesForDriver(DriverId driverId);
    Schedule cancelSchedule(ScheduleId id);
}