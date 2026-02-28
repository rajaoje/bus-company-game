// application/usecase/DriverManagementService.java
package com.busgame.application.usecase;

import com.busgame.domain.exception.DriverNotFoundException;
import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverId;
import com.busgame.domain.model.DriverStatus;
import com.busgame.domain.port.in.DriverManagementUseCase;
import com.busgame.domain.port.out.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Orchestrateur du use case de gestion des conducteurs.
 * Meme structure que FleetManagementService — tu vois que le pattern
 * se repete naturellement d'une feature a l'autre.
 */
@Service
@Transactional
public class DriverManagementService implements DriverManagementUseCase {

    private final DriverRepository driverRepository;

    public DriverManagementService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @Override
    public Driver hireDriver(String firstName, String lastName, String email) {
        // Regle metier : on ne peut pas avoir deux conducteurs avec le meme email
        if (driverRepository.existsByEmail(email)) {
            throw new IllegalArgumentException(
                    "Un conducteur avec l'email " + email + " existe deja."
            );
        }
        Driver driver = Driver.hire(firstName, lastName, email);
        return driverRepository.save(driver);
    }

    @Override
    @Transactional(readOnly = true)
    public Driver getDriver(DriverId id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Driver> getDriversByStatus(DriverStatus status) {
        return driverRepository.findByStatus(status);
    }

    @Override
    public Driver sendDriverOnLeave(DriverId id) {
        Driver driver = getDriver(id);
        driver.goOnLeave();
        return driverRepository.save(driver);
    }

    @Override
    public Driver returnDriverFromLeave(DriverId id) {
        Driver driver = getDriver(id);
        driver.returnFromLeave();
        return driverRepository.save(driver);
    }

    @Override
    public Driver suspendDriver(DriverId id) {
        Driver driver = getDriver(id);
        driver.suspend();
        return driverRepository.save(driver);
    }

    @Override
    public Driver reinstateDriver(DriverId id) {
        Driver driver = getDriver(id);
        driver.reinstate();
        return driverRepository.save(driver);
    }
}