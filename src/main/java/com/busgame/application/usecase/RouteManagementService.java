// application/usecase/RouteManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.RouteNotFoundException;
import com.busgame.domain.model.Route;
import com.busgame.domain.model.RouteId;
import com.busgame.domain.port.in.RouteManagementUseCase;
import com.busgame.domain.port.out.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Apres Niveau 2 GTFS : RouteManagementService est allege.
 * - Plus de addStop() / removeStop() — ces operations passent
 *   desormais par TripManagementService via addStopTime().
 * - Route ne gere plus que son identite : nom, shortName, type.
 */
@Service
@Transactional
public class RouteManagementService implements RouteManagementUseCase {

    private final RouteRepository routeRepository;

    public RouteManagementService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Route createRoute(String name, String description,
                             String shortName) {
        Route route = Route.create(name, description, shortName);
        return routeRepository.save(route);
    }

    @Override
    @Transactional(readOnly = true)
    public Route getRoute(RouteId id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Override
    public void deleteRoute(RouteId id) {
        // Verifier que la route existe avant de supprimer
        routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException(id));
        routeRepository.delete(id);
    }
}