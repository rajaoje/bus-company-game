// infrastructure/web/MaintenanceRecordResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.MaintenanceRecord;
import com.busgame.domain.model.MaintenanceStatus;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceRecordResponseMapper {

    public MaintenanceRecordResponse toResponse(MaintenanceRecord record) {
        Double duration = record.getStatus() == MaintenanceStatus.COMPLETED
                ? record.getActualDurationHours()
                : null;

        return new MaintenanceRecordResponse(
                record.getId().value(),
                record.getBusId().value(),
                record.getStartTime(),
                record.getScheduledEndTime(),
                record.getActualEndTime(),
                record.getCause(),
                record.getStatus(),
                duration
        );
    }
}