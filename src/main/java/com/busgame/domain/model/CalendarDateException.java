// domain/model/CalendarDateException.java
package com.busgame.domain.model;

/**
 * GTFS : exception_type
 * ADDED  = service ajoute ce jour (normalement inactif)
 * REMOVED = service supprime ce jour (normalement actif)
 */
public enum CalendarDateException {
    ADDED,
    REMOVED
}