// domain/port/in/StopManagementUseCase.java
package com.busgame.domain.port.in;

import com.busgame.domain.model.Stop;
import com.busgame.domain.model.StopId;

import java.util.List;

public interface StopManagementUseCase {
    Stop createStop(String name, double latitude, double longitude);
    Stop getStop(StopId id);
    List<Stop> getAllStops();
    void deleteStop(StopId id);
}