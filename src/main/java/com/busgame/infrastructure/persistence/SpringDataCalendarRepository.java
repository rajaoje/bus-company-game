// infrastructure/persistence/SpringDataCalendarRepository.java
package com.busgame.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataCalendarRepository
        extends JpaRepository<CalendarJpaEntity, UUID> {}