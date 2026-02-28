// infrastructure/web/StopResponse.java
package com.busgame.infrastructure.web;

import java.util.UUID;

// infrastructure/web/StopResponse.java
public record StopResponse(
        UUID id,
        String name,
        double latitude,
        double longitude
) {}