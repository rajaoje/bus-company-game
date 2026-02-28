// infrastructure/persistence/DriverMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Driver;
import com.busgame.domain.model.DriverId;
import org.springframework.stereotype.Component;

@Component
public class DriverMapper {

    public DriverJpaEntity toEntity(Driver driver) {
        return new DriverJpaEntity(
                driver.getId().value(),
                driver.getFirstName(),
                driver.getLastName(),
                driver.getEmail(),
                driver.getStatus(),
                driver.getWeeklyHoursWorked()
        );
    }

    public Driver toDomain(DriverJpaEntity entity) {
        return Driver.reconstitute(
                new DriverId(entity.getId()),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getWeeklyHoursWorked()
        );
    }
}