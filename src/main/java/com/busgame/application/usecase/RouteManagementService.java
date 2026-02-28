// application/usecase/RouteManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.RouteNotFoundException;
import com.busgame.domain.model.Distance;
import com.busgame.domain.model.Route;
import com.busgame.domain.model.RouteId;
import com.busgame.domain.model.StopId;
import com.busgame.domain.port.in.RouteManagementUseCase;
import com.busgame.domain.port.out.RouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RouteManagementService implements RouteManagementUseCase {

    private final RouteRepository routeRepository;

    public RouteManagementService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public Route createRoute(String name, String description, String shortName) {
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
    public Route addStop(RouteId routeId, String name,
                         Distance distanceFromPrevious,
                         double latitude, double longitude) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException(routeId));
        route.addStop(name, distanceFromPrevious, latitude, longitude);
        return routeRepository.save(route);
    }

    @Override
    public Route removeStop(RouteId routeId, StopId stopId) {
        Route route = getRoute(routeId);
        route.removeStop(stopId);
        return routeRepository.save(route);
    }
}