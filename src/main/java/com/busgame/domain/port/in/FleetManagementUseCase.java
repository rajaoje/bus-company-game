// domain/port/in/FleetManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusId;
import com.busgame.domain.model.BusStatus;

import java.util.List;

/**
 * Port d'entrée : ce que l'extérieur (API REST, CLI...) peut demander
 * concernant la gestion de la flotte.
 * Définir cette interface nous permet de découpler la couche web
 * de l'implémentation du use case.
 */
public interface FleetManagementUseCase {
    Bus registerBus(String model, int capacity, String busNumber);
    Bus getBus(BusId id);
    List<Bus> getAllBuses();
    List<Bus> getBusesByStatus(BusStatus status);
    Bus sendBusToMaintenance(BusId id);
    Bus returnBusFromMaintenance(BusId id);
    void retireBus(BusId id);
}