// domain/exception/DriverNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.DriverId;

public class DriverNotFoundException extends RuntimeException {
    public DriverNotFoundException(DriverId id) {
        super("Conducteur introuvable avec l'identifiant : " + id);
    }
}