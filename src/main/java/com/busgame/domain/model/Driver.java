// domain/model/Driver.java
package com.busgame.domain.model;

import java.util.UUID;

/**
 * Entite racine (Aggregate Root) du domaine Driver.
 *
 * Meme philosophie que Bus : le constructeur est prive,
 * on passe par des factory methods pour garantir les invariants,
 * et l'etat ne peut etre modifie que via des methodes metier explicites.
 */
public class Driver {

    private final DriverId id;
    private String firstName;
    private String lastName;
    private String email;
    private DriverStatus status;

    /**
     * Le cumul d'heures conduites cette semaine.
     * On le stocke directement sur l'entite plutot que de le recalculer
     * dynamiquement pour une raison de performance et de simplicite :
     * dans un jeu, le moteur de simulation met ce chiffre a jour
     * apres chaque trajet, ce qui est plus efficace que de parcourir
     * tout l'historique des assignations a chaque verification.
     * C'est le compromis dont on avait parle dans la description de la feature.
     */
    private double weeklyHoursWorked;

    // Limite legale hebdomadaire en heures (simplifie pour le jeu)
    private static final double MAX_WEEKLY_HOURS = 48.0;

    private Driver(DriverId id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.status = DriverStatus.AVAILABLE;
        this.weeklyHoursWorked = 0.0;
    }

    /**
     * Factory method : embauche d'un nouveau conducteur.
     * On valide les donnees avant que l'objet n'existe — un conducteur
     * mal configure ne devrait jamais pouvoir etre cree.
     */
    public static Driver hire(String firstName, String lastName, String email) {
        if (firstName == null || firstName.isBlank())
            throw new IllegalArgumentException("Le prenom est obligatoire.");
        if (lastName == null || lastName.isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire.");
        if (email == null || !email.contains("@"))
            throw new IllegalArgumentException("L'email est invalide.");

        return new Driver(new DriverId(UUID.randomUUID()), firstName, lastName, email);
    }

    /**
     * Methode de reconstitution : meme pattern que Bus.reconstitute().
     * Utilisee uniquement pour reconstruire un Driver depuis la base de donnees.
     */
    public static Driver reconstitute(DriverId id, String firstName, String lastName,
                                      String email, DriverStatus status, double weeklyHoursWorked) {
        Driver driver = new Driver(id, firstName, lastName, email);
        driver.status = status;
        driver.weeklyHoursWorked = weeklyHoursWorked;
        return driver;
    }

    /**
     * Prendre son service — transition AVAILABLE → ON_DUTY.
     * La regle : seul un conducteur disponible peut prendre un service.
     */
    public void startDuty() {
        if (this.status != DriverStatus.AVAILABLE)
            throw new IllegalStateException(
                    "Le conducteur doit etre disponible pour prendre un service. Statut actuel : " + this.status
            );
        this.status = DriverStatus.ON_DUTY;
    }

    /**
     * Terminer son service — transition ON_DUTY → AVAILABLE.
     * On en profite pour enregistrer les heures effectuees.
     */
    public void endDuty(double hoursWorked) {
        if (this.status != DriverStatus.ON_DUTY)
            throw new IllegalStateException("Le conducteur n'est pas en service.");
        if (hoursWorked < 0)
            throw new IllegalArgumentException("Les heures travaillees ne peuvent pas etre negatives.");

        this.weeklyHoursWorked += hoursWorked;
        this.status = DriverStatus.AVAILABLE;
    }

    /**
     * Partir en conge — transition AVAILABLE → ON_LEAVE.
     */
    public void goOnLeave() {
        if (this.status != DriverStatus.AVAILABLE)
            throw new IllegalStateException(
                    "Seul un conducteur disponible peut partir en conge. Statut actuel : " + this.status
            );
        this.status = DriverStatus.ON_LEAVE;
    }

    /**
     * Retour de conge — transition ON_LEAVE → AVAILABLE.
     */
    public void returnFromLeave() {
        if (this.status != DriverStatus.ON_LEAVE)
            throw new IllegalStateException("Le conducteur n'est pas en conge.");
        this.status = DriverStatus.AVAILABLE;
    }

    /**
     * Suspension — uniquement depuis AVAILABLE ou ON_LEAVE.
     * On ne peut pas suspendre quelqu'un au volant : il faut d'abord
     * le retirer du service. Cette regle protege la coherence du jeu.
     */
    public void suspend() {
        if (this.status == DriverStatus.ON_DUTY)
            throw new IllegalStateException(
                    "Impossible de suspendre un conducteur en service. Terminez son service d'abord."
            );
        if (this.status == DriverStatus.SUSPENDED)
            throw new IllegalStateException("Le conducteur est deja suspendu.");
        this.status = DriverStatus.SUSPENDED;
    }

    /**
     * Reintegration — transition SUSPENDED → AVAILABLE.
     * Le nom "reinstate" est intentionnellement explicite : c'est un
     * acte delibere de gestion RH, pas un simple changement de statut.
     */
    public void reinstate() {
        if (this.status != DriverStatus.SUSPENDED)
            throw new IllegalStateException("Seul un conducteur suspendu peut etre reintegre.");
        this.status = DriverStatus.AVAILABLE;
    }

    /**
     * Verification : le conducteur peut-il encore conduire cette semaine ?
     * Utile avant d'assigner un conducteur a un horaire (Feature 4).
     */
    public boolean hasAvailableHoursFor(double requiredHours) {
        return (this.weeklyHoursWorked + requiredHours) <= MAX_WEEKLY_HOURS;
    }

    /**
     * Remise a zero du compteur hebdomadaire.
     * Sera appele par le moteur de simulation chaque debut de semaine.
     */
    public void resetWeeklyHours() {
        this.weeklyHoursWorked = 0.0;
    }

    // Getters
    public DriverId getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public DriverStatus getStatus() { return status; }
    public double getWeeklyHoursWorked() { return weeklyHoursWorked; }
    public double getMaxWeeklyHours() { return MAX_WEEKLY_HOURS; }
}