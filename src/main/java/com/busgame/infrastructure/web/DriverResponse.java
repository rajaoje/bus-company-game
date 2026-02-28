// infrastructure/web/DriverResponse.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.DriverStatus;
import java.util.UUID;

public record DriverResponse(
        UUID id,
        String firstName,
        String lastName,
        String email,
        DriverStatus status,
        double weeklyHoursWorked,
        double maxWeeklyHours
) {}