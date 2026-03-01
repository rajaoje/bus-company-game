package com.busgame.infrastructure.persistence;
import com.busgame.domain.model.CalendarDateException;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;
// infrastructure/persistence/CalendarDateJpaEntity.java
@Entity
@Table(name = "calendar_dates")
public class CalendarDateJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name = "service_id", nullable = false)
    private UUID serviceId;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "exception_type", nullable = false)
    private CalendarDateException exceptionType;

    protected CalendarDateJpaEntity() {}

    public CalendarDateJpaEntity(UUID id, UUID serviceId,
                                 LocalDate date,
                                 CalendarDateException exceptionType) {
        this.id            = id;
        this.serviceId     = serviceId;
        this.date          = date;
        this.exceptionType = exceptionType;
    }

    public UUID getId()                          { return id; }
    public UUID getServiceId()                   { return serviceId; }
    public LocalDate getDate()                   { return date; }
    public CalendarDateException getExceptionType() { return exceptionType; }
}