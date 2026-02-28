// infrastructure/web/DriverResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Driver;
import org.springframework.stereotype.Component;

@Component
public class DriverResponseMapper {
    public DriverResponse toResponse(Driver driver) {
        return new DriverResponse(
                driver.getId().value(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getEmail(),
                driver.getStatus(),
                driver.getWeeklyHoursWorked(),
                driver.getMaxWeeklyHours()
        );
    }
}