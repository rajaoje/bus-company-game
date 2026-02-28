// infrastructure/web/CreateRouteRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.NotBlank;

public record CreateRouteRequest(
        @NotBlank(message = "Le nom du parcours est obligatoire")
        String name,
        String description,
        // Numero court de ligne — ex: "12", "A", "Express"
        String shortName
) {}