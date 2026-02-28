// infrastructure/web/HireDriverRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record HireDriverRequest(
        @NotBlank(message = "Le prenom est obligatoire")
        String firstName,

        @NotBlank(message = "Le nom est obligatoire")
        String lastName,

        @Email(message = "L'email doit etre valide")
        @NotBlank(message = "L'email est obligatoire")
        String email
) {}