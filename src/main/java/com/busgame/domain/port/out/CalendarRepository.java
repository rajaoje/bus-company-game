// domain/port/out/CalendarRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.Calendar;
import com.busgame.domain.model.ServiceId;

import java.util.List;
import java.util.Optional;

public interface CalendarRepository {
    Calendar save(Calendar calendar);
    Optional<Calendar> findById(ServiceId id);
    List<Calendar> findAll();
}