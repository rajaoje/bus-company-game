// infrastructure/persistence/BusRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.BusStatus;
import com.busgame.domain.port.out.BusRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * L'adapter de persistance : implémente le port de sortie du domaine
 * en utilisant Spring Data JPA.
 * C'est le "traducteur" entre le monde domaine et le monde technique.
 */
@Component
public class BusRepositoryAdapter implements BusRepository {

    private final SpringDataBusRepository springDataRepo;
    private final BusMapper mapper;

    public BusRepositoryAdapter(SpringDataBusRepository springDataRepo, BusMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public Bus save(Bus bus) {
        BusJpaEntity entity = mapper.toEntity(bus);
        BusJpaEntity saved = springDataRepo.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Bus> findById(BusId id) {
        return springDataRepo.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Bus> findAll() {
        return springDataRepo.findAll().stream()
                .map(mapper::toDomain)
                .toList(); // Java 16+ — plus concis que Collectors.toList()
    }

    @Override
    public List<Bus> findByStatus(BusStatus status) {
        return springDataRepo.findByStatus(status).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByBusNumber(String busNumber) {
        return springDataRepo.existsByBusNumber(busNumber);
    }

    @Override
    public void delete(BusId id) {
        springDataRepo.deleteById(id.value());
    }

    @Override
    public boolean existsById(BusId id) {
        return springDataRepo.existsById(id.value());
    }
}