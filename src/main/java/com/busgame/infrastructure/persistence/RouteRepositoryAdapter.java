// infrastructure/persistence/RouteRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Route;
import com.busgame.domain.model.RouteId;
import com.busgame.domain.port.out.RouteRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class RouteRepositoryAdapter implements RouteRepository {

    private final SpringDataRouteRepository springDataRepo;
    private final RouteMapper mapper;

    public RouteRepositoryAdapter(SpringDataRouteRepository springDataRepo,
                                  RouteMapper mapper) {
        this.springDataRepo = springDataRepo;
        this.mapper = mapper;
    }

    @Override
    public Route save(Route route) {
        return mapper.toDomain(springDataRepo.save(mapper.toEntity(route)));
    }

    @Override
    public Optional<Route> findById(RouteId id) {
        return springDataRepo.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Route> findAll() {
        return springDataRepo.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public void delete(RouteId id) {
        springDataRepo.deleteById(id.value());
    }
}