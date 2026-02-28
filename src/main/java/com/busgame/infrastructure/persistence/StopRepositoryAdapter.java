// infrastructure/persistence/StopRepositoryAdapter.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Stop;
import com.busgame.domain.model.StopId;
import com.busgame.domain.port.out.StopRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StopRepositoryAdapter implements StopRepository {

    private final SpringDataStopRepository springDataRepo;

    public StopRepositoryAdapter(SpringDataStopRepository springDataRepo) {
        this.springDataRepo = springDataRepo;
    }

    @Override
    public Stop save(Stop stop) {
        StopJpaEntity entity = new StopJpaEntity(
                stop.getId().value(),
                stop.getName(),
                stop.getLatitude(),
                stop.getLongitude()
        );
        StopJpaEntity saved = springDataRepo.save(entity);
        return Stop.reconstitute(
                new StopId(saved.getId()),
                saved.getName(),
                saved.getLatitude(),
                saved.getLongitude()
        );
    }

    @Override
    public Optional<Stop> findById(StopId id) {
        return springDataRepo.findById(id.value())
                .map(e -> Stop.reconstitute(
                        new StopId(e.getId()),
                        e.getName(),
                        e.getLatitude(),
                        e.getLongitude()
                ));
    }

    @Override
    public List<Stop> findAll() {
        return springDataRepo.findAll()
                .stream()
                .map(e -> Stop.reconstitute(
                        new StopId(e.getId()),
                        e.getName(),
                        e.getLatitude(),
                        e.getLongitude()
                ))
                .toList();
    }

    @Override
    public void delete(StopId id) {
        springDataRepo.deleteById(id.value());
    }
}