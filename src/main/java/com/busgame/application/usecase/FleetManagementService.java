// application/usecase/FleetManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.BusNotFoundException;
import com.busgame.domain.exception.DuplicateBusNumberException;
import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.BusStatus;
import com.busgame.domain.port.in.FleetManagementUseCase;
import com.busgame.domain.port.out.BusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du use case de gestion de flotte.
 * C'est ici que Spring intervient (@Service, @Transactional),
 * mais uniquement dans cette couche — jamais dans le domaine.
 *
 * Ce service est le "chef d'orchestre" : il coordonne les entités
 * du domaine et les repositories sans contenir lui-même de logique métier.
 * La logique métier est dans les entités (Bus, Driver, etc.).
 */
@Service
@Transactional
public class FleetManagementService implements FleetManagementUseCase {

    private final BusRepository busRepository;

    // Injection par constructeur — bonne pratique avec Spring
    public FleetManagementService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    @Override
    public Bus registerBus(String model, int capacity, String busNumber) {
        // Verifier l'unicite du numero avant de creer le bus
        if (busRepository.existsByBusNumber(busNumber.trim().toUpperCase()))
            throw new DuplicateBusNumberException(busNumber);

        Bus bus = Bus.register(model, capacity, busNumber);
        return busRepository.save(bus);
    }

    @Override
    @Transactional(readOnly = true)
    public Bus getBus(BusId id) {
        return busRepository.findById(id)
                .orElseThrow(() -> new BusNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> getAllBuses() {
        return busRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Bus> getBusesByStatus(BusStatus status) {
        return busRepository.findByStatus(status);
    }

    @Override
    public Bus sendBusToMaintenance(BusId id) {
        Bus bus = getBus(id);
        bus.sendToMaintenance(); // La règle métier est dans l'entité
        return busRepository.save(bus);
    }

    @Override
    public Bus returnBusFromMaintenance(BusId id) {
        Bus bus = getBus(id);
        bus.returnFromMaintenance();
        return busRepository.save(bus);
    }

    @Override
    public void retireBus(BusId id) {
        Bus bus = getBus(id);
        bus.retire();
        busRepository.save(bus);
    }
}