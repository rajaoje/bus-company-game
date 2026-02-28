// infrastructure/persistence/MaintenanceRecordMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceRecordMapper {

    public MaintenanceRecordJpaEntity toEntity(MaintenanceRecord record) {
        return new MaintenanceRecordJpaEntity(
                record.getId().value(),
                record.getBusId().value(),
                record.getStartTime(),
                record.getScheduledEndTime(),
                record.getActualEndTime(),
                record.getCause(),
                record.getStatus()
        );
    }

    public MaintenanceRecord toDomain(MaintenanceRecordJpaEntity entity) {
        return MaintenanceRecord.reconstitute(
                new MaintenanceRecordId(entity.getId()),
                new BusId(entity.getBusId()),
                entity.getStartTime(),
                entity.getScheduledEndTime(),
                entity.getActualEndTime(),
                entity.getCause(),
                entity.getStatus()
        );
    }
}