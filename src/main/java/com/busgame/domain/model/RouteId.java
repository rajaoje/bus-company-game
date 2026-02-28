// domain/model/RouteId.java
package com.busgame.domain.model;

import java.util.UUID;

public record RouteId(UUID value) {
    public RouteId {
        if (value == null) throw new IllegalArgumentException("RouteId ne peut pas etre null.");
    }
}