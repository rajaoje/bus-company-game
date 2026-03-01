// domain/exception/StopNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.StopId;

public class StopNotFoundException extends RuntimeException {
    public StopNotFoundException(StopId id) {
        super("Arret introuvable : " + id);
    }
}