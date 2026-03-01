// infrastructure/persistence/SpringDataCalendarDateRepository.java
package com.busgame.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataCalendarDateRepository
        extends JpaRepository<CalendarDateJpaEntity, UUID> {

    List<CalendarDateJpaEntity> findByServiceId(UUID serviceId);
}