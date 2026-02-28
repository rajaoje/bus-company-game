// infrastructure/persistence/TripRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import com.busgame.domain.port.out.TripRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TripRepositoryAdapter implements TripRepository {

    private final SpringDataTripRepository springDataRepo;
    private final TripMapper mapper;

    public TripRepositoryAdapter(SpringDataTripRepository springDataRepo,
                                 TripMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public Trip save(Trip trip) {
        return mapper.toDomain(
                springDataRepo.save(mapper.toEntity(trip)));
    }

    @Override
    public Optional<Trip> findById(TripId id) {
        return springDataRepo.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Trip> findAll() {
        return springDataRepo.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Trip> findByRouteId(RouteId routeId) {
        return springDataRepo.findByRouteId(routeId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(TripId id) {
        springDataRepo.deleteById(id.value());
    }
}