// infrastructure/persistence/MaintenanceRecordRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import com.busgame.domain.port.out.MaintenanceRecordRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class MaintenanceRecordRepositoryAdapter implements MaintenanceRecordRepository {

    private final SpringDataMaintenanceRecordRepository springDataRepo;
    private final MaintenanceRecordMapper mapper;

    public MaintenanceRecordRepositoryAdapter(
            SpringDataMaintenanceRecordRepository springDataRepo,
            MaintenanceRecordMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public MaintenanceRecord save(MaintenanceRecord record) {
        return mapper.toDomain(springDataRepo.save(mapper.toEntity(record)));
    }

    @Override
    public Optional<MaintenanceRecord> findById(MaintenanceRecordId id) {
        return springDataRepo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public List<MaintenanceRecord> findByBusId(BusId busId) {
        return springDataRepo.findByBusId(busId.value())
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<MaintenanceRecord> findActiveByBusId(BusId busId) {
        return springDataRepo.findByBusIdAndStatus(busId.value(), MaintenanceStatus.ACTIVE)
                .map(mapper::toDomain);
    }

    @Override
    public List<MaintenanceRecord> findRecordsReadyToComplete(LocalDateTime gameTime) {
        return springDataRepo.findRecordsReadyToComplete(gameTime)
                .stream().map(mapper::toDomain).toList();
    }
}