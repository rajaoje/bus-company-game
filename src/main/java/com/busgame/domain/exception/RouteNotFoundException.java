// domain/exception/RouteNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.RouteId;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(RouteId id) {
        super("Parcours introuvable avec l'identifiant : " + id);
    }
}