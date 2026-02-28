package com.busgame.infrastructure.web;

import com.busgame.domain.model.Stop;
import org.springframework.stereotype.Component;

@Component
public class StopResponseMapper {
    public StopResponse toResponse(Stop stop) {
        return new StopResponse(
                stop.getId().value(),
                stop.getName(),
                stop.getLatitude(),
                stop.getLongitude()
        );
    }
}
