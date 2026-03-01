package com.busgame.infrastructure.persistence;


import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.UUID;

// infrastructure/persistence/CalendarJpaEntity.java
@Entity
@Table(name = "calendars")
public class CalendarJpaEntity {

    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    // Jours actifs stockes comme colonnes booleennes — lisible en SQL
    @Column(name = "monday",    nullable = false)
    private boolean monday;
    @Column(name = "tuesday",   nullable = false)
    private boolean tuesday;
    @Column(name = "wednesday", nullable = false)
    private boolean wednesday;
    @Column(name = "thursday",  nullable = false)
    private boolean thursday;
    @Column(name = "friday",    nullable = false)
    private boolean friday;
    @Column(name = "saturday",  nullable = false)
    private boolean saturday;
    @Column(name = "sunday",    nullable = false)
    private boolean sunday;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    protected CalendarJpaEntity() {}

    public CalendarJpaEntity(UUID id,
                             boolean monday, boolean tuesday,
                             boolean wednesday, boolean thursday,
                             boolean friday, boolean saturday,
                             boolean sunday,
                             LocalDate startDate, LocalDate endDate) {
        this.id        = id;
        this.monday    = monday;
        this.tuesday   = tuesday;
        this.wednesday = wednesday;
        this.thursday  = thursday;
        this.friday    = friday;
        this.saturday  = saturday;
        this.sunday    = sunday;
        this.startDate = startDate;
        this.endDate   = endDate;
    }

    public UUID getId()          { return id; }
    public boolean isMonday()    { return monday; }
    public boolean isTuesday()   { return tuesday; }
    public boolean isWednesday() { return wednesday; }
    public boolean isThursday()  { return thursday; }
    public boolean isFriday()    { return friday; }
    public boolean isSaturday()  { return saturday; }
    public boolean isSunday()    { return sunday; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate()   { return endDate; }
}