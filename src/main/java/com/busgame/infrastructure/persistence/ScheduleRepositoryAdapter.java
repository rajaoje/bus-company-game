// infrastructure/persistence/ScheduleRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import com.busgame.domain.port.out.ScheduleRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ScheduleRepositoryAdapter implements ScheduleRepository {

    private final SpringDataScheduleRepository springDataRepo;
    private final ScheduleMapper mapper;

    public ScheduleRepositoryAdapter(SpringDataScheduleRepository springDataRepo,
                                     ScheduleMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public Schedule save(Schedule schedule) {
        return mapper.toDomain(springDataRepo.save(mapper.toEntity(schedule)));
    }

    @Override
    public Optional<Schedule> findById(ScheduleId id) {
        return springDataRepo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<Schedule> findAll() {
        return springDataRepo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Schedule> findByBusId(BusId busId) {
        return springDataRepo.findByBusId(busId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Schedule> findByDriverId(DriverId driverId) {
        return springDataRepo.findByDriverId(driverId.value()).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<Schedule> findActiveSchedulesForBusInPeriod(BusId busId,
                                                            LocalDateTime start,
                                                            LocalDateTime end) {
        return springDataRepo.findActiveForBusInPeriod(busId.value(), start, end)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Schedule> findActiveSchedulesForDriverInPeriod(DriverId driverId,
                                                               LocalDateTime start,
                                                               LocalDateTime end) {
        return springDataRepo.findActiveForDriverInPeriod(driverId.value(), start, end)
                .stream().map(mapper::toDomain).toList();
    }

    // Methodes a ajouter dans ScheduleRepositoryAdapter.java

    @Override
    public List<Schedule> findSchedulesToStart(LocalDateTime gameTime) {
        return springDataRepo.findSchedulesToStart(gameTime)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Schedule> findSchedulesToComplete(LocalDateTime gameTime) {
        return springDataRepo.findSchedulesToComplete(gameTime)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Schedule> findByStatus(ScheduleStatus status) {
        return springDataRepo.findByStatus(status)
                .stream().map(mapper::toDomain).toList();
    }
}