// domain/exception/MaintenanceRecordNotFoundException.java
package com.busgame.domain.exception;

import com.busgame.domain.model.MaintenanceRecordId;

public class MaintenanceRecordNotFoundException extends RuntimeException {
    public MaintenanceRecordNotFoundException(MaintenanceRecordId id) {
        super("Dossier de maintenance introuvable : " + id);
    }
}