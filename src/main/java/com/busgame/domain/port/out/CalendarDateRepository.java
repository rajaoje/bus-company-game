// domain/port/out/CalendarDateRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.CalendarDate;
import com.busgame.domain.model.CalendarDateId;
import com.busgame.domain.model.ServiceId;

import java.util.List;

public interface CalendarDateRepository {
    CalendarDate save(CalendarDate calendarDate);
    List<CalendarDate> findByServiceId(ServiceId serviceId);
}