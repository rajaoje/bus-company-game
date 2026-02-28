// infrastructure/persistence/MaintenanceRecordJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.MaintenanceCause;
import com.busgame.domain.model.MaintenanceStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "maintenance_records")
public class MaintenanceRecordJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "bus_id", nullable = false)
    private UUID busId;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "scheduled_end_time", nullable = false)
    private LocalDateTime scheduledEndTime;

    // Nullable : null tant que la maintenance n'est pas terminee
    @Column(name = "actual_end_time")
    private LocalDateTime actualEndTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceCause cause;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MaintenanceStatus status;

    protected MaintenanceRecordJpaEntity() {}

    public MaintenanceRecordJpaEntity(UUID id, UUID busId, LocalDateTime startTime,
                                      LocalDateTime scheduledEndTime,
                                      LocalDateTime actualEndTime,
                                      MaintenanceCause cause, MaintenanceStatus status) {
        this.id = id;
        this.busId = busId;
        this.startTime = startTime;
        this.scheduledEndTime = scheduledEndTime;
        this.actualEndTime = actualEndTime;
        this.cause = cause;
        this.status = status;
    }

    public UUID getId() { return id; }
    public UUID getBusId() { return busId; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getScheduledEndTime() { return scheduledEndTime; }
    public LocalDateTime getActualEndTime() { return actualEndTime; }
    public MaintenanceCause getCause() { return cause; }
    public MaintenanceStatus getStatus() { return status; }
}