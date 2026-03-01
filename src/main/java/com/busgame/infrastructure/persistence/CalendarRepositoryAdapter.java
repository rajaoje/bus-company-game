// infrastructure/persistence/CalendarRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Calendar;
import com.busgame.domain.model.ServiceId;
import com.busgame.domain.port.out.CalendarRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CalendarRepositoryAdapter implements CalendarRepository {

    private final SpringDataCalendarRepository springDataRepo;
    private final CalendarMapper mapper;

    public CalendarRepositoryAdapter(
            SpringDataCalendarRepository springDataRepo,
            CalendarMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper         = mapper;
    }

    @Override
    public Calendar save(Calendar calendar) {
        return mapper.toDomain(
                springDataRepo.save(mapper.toEntity(calendar)));
    }

    @Override
    public Optional<Calendar> findById(ServiceId id) {
        return springDataRepo.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Calendar> findAll() {
        return springDataRepo.findAll()
                .stream().map(mapper::toDomain).toList();
    }
}