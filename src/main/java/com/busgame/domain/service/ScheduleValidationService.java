// domain/service/ScheduleValidationService.java
package com.busgame.domain.service;

import com.busgame.domain.model.Bus;
import com.busgame.domain.model.BusStatus;
import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverStatus;

/**
 * Domain Service : encode les regles metier de validation d'un horaire
 * qui impliquent plusieurs agregats simultanement.
 *
 * Ce n'est pas un @Service Spring — c'est de la logique metier pure.
 * Il sera instancie et appele par le Use Case (couche application).
 *
 * La distinction est importante : ce service EXPRIME des regles metier,
 * il ne les orchestre pas. L'orchestration, c'est le role du Use Case.
 */
public class ScheduleValidationService {

    /**
     * Verifie qu'un bus peut etre assigne a un nouvel horaire.
     */
    public void validateBusAvailability(Bus bus) {
        if (bus.getStatus() != BusStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Le bus " + bus.getId() + " n'est pas disponible. " +
                            "Statut actuel : " + bus.getStatus()
            );
        }
    }

    /**
     * Verifie qu'un conducteur peut etre assigne a un nouvel horaire,
     * en tenant compte de ses heures hebdomadaires restantes.
     */
    public void validateDriverAvailability(Driver driver, double requiredHours) {
        if (driver.getStatus() != DriverStatus.AVAILABLE) {
            throw new IllegalStateException(
                    "Le conducteur " + driver.getId() + " n'est pas disponible. " +
                            "Statut actuel : " + driver.getStatus()
            );
        }
        if (!driver.hasAvailableHoursFor(requiredHours)) {
            throw new IllegalStateException(
                    "Le conducteur " + driver.getId() +
                            " a atteint son maximum d'heures hebdomadaires. " +
                            "Heures restantes : " +
                            (driver.getMaxWeeklyHours() - driver.getWeeklyHoursWorked())
            );
        }
    }
}