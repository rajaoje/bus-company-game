// domain/model/MaintenanceCause.java
package com.busgame.domain.model;

/**
 * La cause d'une maintenance.
 * On distingue les pannes aleatoires (simulees) des maintenances
 * declenchees manuellement par le joueur.
 * Cette distinction sera utile pour les statistiques et les couts futurs.
 */
public enum MaintenanceCause {
    BREAKDOWN,  // Panne aleatoire declenchee par le moteur de simulation
    MANUAL      // Maintenance declenchee manuellement par le joueur
}