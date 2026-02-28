// domain/model/Distance.java
package com.busgame.domain.model;

/**
 * Value Object representant une distance en kilometres.
 * On l'encapsule pour eviter de manipuler des doubles bruts
 * qui pourraient representer n'importe quoi (km, miles, minutes...).
 */
public record Distance(double kilometers) {
    public Distance {
        if (kilometers < 0)
            throw new IllegalArgumentException("Une distance ne peut pas etre negative.");
    }

    // Additionner deux distances — utile pour calculer la distance totale du parcours
    public Distance add(Distance other) {
        return new Distance(this.kilometers + other.kilometers);
    }

    public static Distance zero() {
        return new Distance(0);
    }
}