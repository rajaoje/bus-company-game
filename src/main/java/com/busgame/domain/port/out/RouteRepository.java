// domain/port/out/RouteRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.Route;
import com.busgame.domain.model.RouteId;

import java.util.List;
import java.util.Optional;

public interface RouteRepository {
    Route save(Route route);
    Optional<Route> findById(RouteId id);
    List<Route> findAll();
    void delete(RouteId id);
}