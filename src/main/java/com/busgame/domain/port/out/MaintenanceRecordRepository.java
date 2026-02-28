// domain/port/out/MaintenanceRecordRepository.java
package com.busgame.domain.port.out;

import com.busgame.domain.model.BusId;
import com.busgame.domain.model.MaintenanceRecord;
import com.busgame.domain.model.MaintenanceRecordId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRecordRepository {
    MaintenanceRecord save(MaintenanceRecord record);
    Optional<MaintenanceRecord> findById(MaintenanceRecordId id);

    // Historique complet pour un bus — utile pour les stats futures
    List<MaintenanceRecord> findByBusId(BusId busId);

    // Le dossier actif d'un bus — un bus ne peut avoir qu'une maintenance a la fois
    Optional<MaintenanceRecord> findActiveByBusId(BusId busId);

    /**
     * Le coeur de la sortie automatique de maintenance :
     * tous les dossiers ACTIVE dont la fin prevue est atteinte.
     */
    List<MaintenanceRecord> findRecordsReadyToComplete(LocalDateTime gameTime);
}