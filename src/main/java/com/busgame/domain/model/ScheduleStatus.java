// domain/model/ScheduleStatus.java
package com.busgame.domain.model;

/**
 * Les etats possibles d'un horaire.
 *
 * PLANNED    : l'horaire est cree mais le service n'a pas encore commence.
 * IN_PROGRESS: le bus est en route, le conducteur est au volant.
 * COMPLETED  : le service s'est termine normalement.
 * CANCELLED  : l'horaire a ete annule avant ou pendant le service.
 *
 * Ces transitions seront utilisees par le moteur de simulation en Feature 5.
 * On les prepare maintenant pour ne pas avoir a modifier le modele plus tard.
 */
public enum ScheduleStatus {
    PLANNED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}