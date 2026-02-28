// infrastructure/web/SetSpeedRequest.java
package com.busgame.infrastructure.web;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record SetSpeedRequest(
        @Min(value = 1, message = "Le multiplicateur minimum est 1")
        @Max(value = 60, message = "Le multiplicateur maximum est 60")
        int multiplier
) {}