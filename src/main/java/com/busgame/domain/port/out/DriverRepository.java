// domain/port/out/DriverRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverId;
import com.busgame.domain.model.DriverStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie : ce dont le domaine a besoin de la persistance.
 * On ajoute findByEmail() car l'email est notre identifiant fonctionnel
 * unique — on en aura besoin pour verifier les doublons a l'embauche.
 */
public interface DriverRepository {
    Driver save(Driver driver);
    Optional<Driver> findById(DriverId id);
    Optional<Driver> findByEmail(String email);
    List<Driver> findAll();
    List<Driver> findByStatus(DriverStatus status);
    boolean existsByEmail(String email);
}