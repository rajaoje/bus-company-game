// domain/port/out/BusRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.BusStatus;

import java.util.List;
import java.util.Optional;

/**
 * Port de sortie : contrat que le domaine impose à la couche infrastructure.
 * Le domaine dit "j'ai besoin de ces opérations" sans se soucier
 * de si c'est H2, PostgreSQL ou un simple Map en mémoire qui répond.
 *
 * C'est l'inversion de dépendances (le D de SOLID) appliquée concrètement.
 */
public interface BusRepository {
    Bus save(Bus bus);
    Optional<Bus> findById(BusId id);
    List<Bus> findAll();
    List<Bus> findByStatus(BusStatus status);
    void delete(BusId id);
    boolean existsById(BusId id);
    boolean existsByBusNumber(String busNumber);
}