// infrastructure/web/MaintenanceRecordResponse.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.MaintenanceCause;
import com.busgame.domain.model.MaintenanceStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record MaintenanceRecordResponse(
        UUID id,
        UUID busId,
        LocalDateTime startTime,
        LocalDateTime scheduledEndTime,
        LocalDateTime actualEndTime,        // null si encore active
        MaintenanceCause cause,
        MaintenanceStatus status,
        Double actualDurationHours          // null si encore active
) {}