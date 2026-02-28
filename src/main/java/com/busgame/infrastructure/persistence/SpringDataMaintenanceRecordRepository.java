// infrastructure/persistence/SpringDataMaintenanceRecordRepository.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataMaintenanceRecordRepository
        extends JpaRepository<MaintenanceRecordJpaEntity, UUID> {

    List<MaintenanceRecordJpaEntity> findByBusId(UUID busId);

    Optional<MaintenanceRecordJpaEntity> findByBusIdAndStatus(
            UUID busId, MaintenanceStatus status);

    @Query("""
        SELECT m FROM MaintenanceRecordJpaEntity m
        WHERE m.status = 'ACTIVE'
        AND m.scheduledEndTime <= :gameTime
    """)
    List<MaintenanceRecordJpaEntity> findRecordsReadyToComplete(
            @Param("gameTime") LocalDateTime gameTime);
}