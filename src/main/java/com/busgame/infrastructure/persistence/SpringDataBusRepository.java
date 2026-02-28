// infrastructure/persistence/SpringDataBusRepository.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.BusStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Interface Spring Data — Spring génère l'implémentation automatiquement.
 * Notez qu'elle travaille avec BusJpaEntity, pas avec Bus (domaine).
 */
public interface SpringDataBusRepository extends JpaRepository<BusJpaEntity, UUID> {
    List<BusJpaEntity> findByStatus(BusStatus status);
    boolean existsByBusNumber(String busNumber);
}