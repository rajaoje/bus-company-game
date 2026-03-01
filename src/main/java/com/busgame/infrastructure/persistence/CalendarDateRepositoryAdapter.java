// infrastructure/persistence/CalendarDateRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.CalendarDate;
import com.busgame.domain.model.ServiceId;
import com.busgame.domain.port.out.CalendarDateRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalendarDateRepositoryAdapter
        implements CalendarDateRepository {

    private final SpringDataCalendarDateRepository springDataRepo;
    private final CalendarDateMapper mapper;

    public CalendarDateRepositoryAdapter(
            SpringDataCalendarDateRepository springDataRepo,
            CalendarDateMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper         = mapper;
    }

    @Override
    public CalendarDate save(CalendarDate calendarDate) {
        return mapper.toDomain(
                springDataRepo.save(mapper.toEntity(calendarDate)));
    }

    @Override
    public List<CalendarDate> findByServiceId(ServiceId serviceId) {
        return springDataRepo.findByServiceId(serviceId.value())
                .stream().map(mapper::toDomain).toList();
    }
}