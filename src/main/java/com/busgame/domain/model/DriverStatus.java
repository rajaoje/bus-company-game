// domain/model/DriverStatus.java
package com.busgame.domain.model;

/**
 * Les quatre etats possibles d'un conducteur.
 *
 * Reflechis aux transitions valides entre ces etats :
 * AVAILABLE  → ON_DUTY      : quand il prend son service
 * ON_DUTY    → AVAILABLE    : quand il termine son service
 * AVAILABLE  → ON_LEAVE     : quand il part en conge
 * ON_LEAVE   → AVAILABLE    : quand il revient de conge
 * AVAILABLE  → SUSPENDED    : quand il est suspendu
 * SUSPENDED  → AVAILABLE    : quand il est reintegre
 *
 * Remarque qu'on ne peut PAS suspendre un conducteur ON_DUTY directement —
 * cette regle sera encodee dans l'entite Driver elle-meme.
 */
public enum DriverStatus {
    AVAILABLE,   // Disponible, peut etre assigne a un service
    ON_DUTY,     // Actuellement au volant
    ON_LEAVE,    // En conge planifie
    SUSPENDED    // Suspendu, ne peut plus etre assigne
}