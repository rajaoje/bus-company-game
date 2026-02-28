// application/usecase/StopManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.model.Stop;
import com.busgame.domain.model.StopId;
import com.busgame.domain.port.in.StopManagementUseCase;
import com.busgame.domain.port.out.StopRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StopManagementService implements StopManagementUseCase {

    private final StopRepository stopRepository;

    public StopManagementService(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    @Override
    public Stop createStop(String name,
                           double latitude, double longitude) {
        Stop stop = Stop.create(name, latitude, longitude);
        return stopRepository.save(stop);
    }

    @Override
    @Transactional(readOnly = true)
    public Stop getStop(StopId id) {
        return stopRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Arret introuvable : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Stop> getAllStops() {
        return stopRepository.findAll();
    }

    @Override
    public void deleteStop(StopId id) {
        stopRepository.delete(id);
    }
}