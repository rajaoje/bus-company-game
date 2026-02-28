// infrastructure/persistence/SpringDataDriverRepository.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data genere automatiquement les requetes SQL depuis
 * les noms de methodes — findByEmail devient "SELECT * FROM drivers WHERE email = ?".
 * C'est une des grandes forces de Spring Data JPA.
 */
public interface SpringDataDriverRepository extends JpaRepository<DriverJpaEntity, UUID> {
    Optional<DriverJpaEntity> findByEmail(String email);
    List<DriverJpaEntity> findByStatus(DriverStatus status);
    boolean existsByEmail(String email);
}