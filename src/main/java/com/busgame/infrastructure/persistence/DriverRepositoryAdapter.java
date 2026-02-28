// infrastructure/persistence/DriverRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverId;
import com.busgame.domain.model.DriverStatus;
import com.busgame.domain.port.out.DriverRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DriverRepositoryAdapter implements DriverRepository {

    private final SpringDataDriverRepository springDataRepo;
    private final DriverMapper mapper;

    public DriverRepositoryAdapter(SpringDataDriverRepository springDataRepo, DriverMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public Driver save(Driver driver) {
        return mapper.toDomain(springDataRepo.save(mapper.toEntity(driver)));
    }

    @Override
    public Optional<Driver> findById(DriverId id) {
        return springDataRepo.findById(id.value()).map(mapper::toDomain);
    }

    @Override
    public Optional<Driver> findByEmail(String email) {
        return springDataRepo.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<Driver> findAll() {
        return springDataRepo.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<Driver> findByStatus(DriverStatus status) {
        return springDataRepo.findByStatus(status).stream().map(mapper::toDomain).toList();
    }

    @Override
    public boolean existsByEmail(String email) {
        return springDataRepo.existsByEmail(email);
    }
}