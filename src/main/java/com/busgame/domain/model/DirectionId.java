// domain/model/DirectionId.java
package com.busgame.domain.model;

/**
 * GTFS : direction_id.
 * 0 = aller, 1 = retour.
 * Permet de distinguer les deux sens d'une ligne.
 */
public enum DirectionId {
    OUTBOUND(0),  // Aller
    INBOUND(1);   // Retour

    private final int gtfsCode;

    DirectionId(int gtfsCode) { this.gtfsCode = gtfsCode; }
    public int getGtfsCode()  { return gtfsCode; }
}