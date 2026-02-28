// infrastructure/persistence/DriverJpaEntity.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.DriverStatus;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "drivers")
public class DriverJpaEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    // unique = true : la contrainte est aussi appliquee au niveau BDD,
    // pas seulement au niveau applicatif. Double securite.
    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DriverStatus status;

    @Column(name = "weekly_hours_worked", nullable = false)
    private double weeklyHoursWorked;

    protected DriverJpaEntity() {}

    public DriverJpaEntity(UUID id, String firstName, String lastName,
                           String email, DriverStatus status, double weeklyHoursWorked) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = status;
        this.weeklyHoursWorked = weeklyHoursWorked;
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public DriverStatus getStatus() { return status; }
    public double getWeeklyHoursWorked() { return weeklyHoursWorked; }
}