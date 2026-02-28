// domain/model/BusStatus.java
package com.busgame.domain.model;

/**
 * Enumération des états possibles d'un bus.
 * Centraliser les états dans un enum évite les "magic strings"
 * et rend les transitions d'état explicites.
 */
public enum BusStatus {
    AVAILABLE,       // Disponible pour être assigné
    IN_SERVICE,      // Actuellement sur une ligne
    IN_MAINTENANCE,  // En réparation ou entretien
    RETIRED          // Retiré de la flotte, ne peut plus être utilisé
}