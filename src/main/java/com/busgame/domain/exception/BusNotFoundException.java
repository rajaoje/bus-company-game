// domain/exception/BusNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.BusId;

/**
 * Exception métier : elle appartient au domaine, pas à Spring.
 * On la traduit ensuite dans l'adapter web en réponse HTTP 404.
 */
public class BusNotFoundException extends RuntimeException {
    public BusNotFoundException(BusId id) {
        super("Bus introuvable avec l'identifiant : " + id);
    }
}