// infrastructure/web/AddStopRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

public record AddStopRequest(
        @NotBlank(message = "Le nom de l'arret est obligatoire")
        String name,
        @PositiveOrZero
        double distanceFromPreviousKm,

        // Coordonnees GPS — optionnelles, defaut 0.0
        double latitude,
        double longitude
) {}