// infrastructure/persistence/SpringDataTripRepository.java
package com.busgame.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataTripRepository
        extends JpaRepository<TripJpaEntity, UUID> {

    List<TripJpaEntity> findByRouteId(UUID routeId);
}