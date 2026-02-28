// domain/port/in/DriverManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverId;
import com.busgame.domain.model.DriverStatus;

import java.util.List;

/**
 * Port d'entree : contrat que l'infrastructure (web, CLI...)
 * utilise pour interagir avec la gestion des conducteurs.
 */
public interface DriverManagementUseCase {
    Driver hireDriver(String firstName, String lastName, String email);
    Driver getDriver(DriverId id);
    List<Driver> getAllDrivers();
    List<Driver> getDriversByStatus(DriverStatus status);
    Driver sendDriverOnLeave(DriverId id);
    Driver returnDriverFromLeave(DriverId id);
    Driver suspendDriver(DriverId id);
    Driver reinstateDriver(DriverId id);
}