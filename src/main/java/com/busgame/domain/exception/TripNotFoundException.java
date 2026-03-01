// domain/exception/TripNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.TripId;

public class TripNotFoundException extends RuntimeException {
    public TripNotFoundException(TripId id) {
        super("Trip introuvable : " + id);
    }
}