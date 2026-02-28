// domain/exception/ScheduleNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.ScheduleId;

public class ScheduleNotFoundException extends RuntimeException {
    public ScheduleNotFoundException(ScheduleId id) {
        super("Horaire introuvable avec l'identifiant : " + id);
    }
}