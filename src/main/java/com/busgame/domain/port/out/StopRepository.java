// domain/port/out/StopRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.Stop;
import com.busgame.domain.model.StopId;

import java.util.List;
import java.util.Optional;

public interface StopRepository {
    Stop save(Stop stop);
    Optional<Stop> findById(StopId id);
    List<Stop> findAll();
    void delete(StopId id);
}