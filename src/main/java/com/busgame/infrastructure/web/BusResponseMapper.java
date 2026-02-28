// infrastructure/web/BusResponseMapper.java
package com.busgame.infrastructure.web;

import com.busgame.domain.model.Bus;
import org.springframework.stereotype.Component;

@Component
public class BusResponseMapper {
    public BusResponse toResponse(Bus bus) {
        return new BusResponse(
                bus.getId().value(),
                bus.getBusNumber(),
                bus.getModel(),
                bus.getCapacity(),
                bus.getStatus(),
                bus.getMileage()

        );
    }
}