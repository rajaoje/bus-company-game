// infrastructure/web/BusResponse.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.BusStatus;
import java.util.UUID;

/**
 * DTO de réponse : ce qu'on expose à l'extérieur.
 * Découplé du modèle domaine — on peut choisir ce qu'on expose
 * sans affecter la structure interne.
 */
public record BusResponse(
        UUID id,
        String busNumber,
        String model,
        int capacity,
        BusStatus status,
        int mileage
) {}