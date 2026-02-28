// infrastructure/web/RegisterBusRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO de requête : record Java 21, immuable et concis.
 * Les annotations de validation garantissent la qualité des données
 * avant même d'atteindre le domaine.
 */
public record RegisterBusRequest(
        @NotBlank(message = "Le modèle est obligatoire")
        String model,

        @Min(value = 1, message = "La capacité doit être au moins 1")
        int capacity,

        @NotBlank(message = "Le numero de bus est obligatoire")
        String busNumber
) {}