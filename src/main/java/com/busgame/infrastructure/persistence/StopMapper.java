// infrastructure/persistence/StopMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.Stop;
import com.busgame.domain.model.StopId;
import org.springframework.stereotype.Component;

@Component
public class StopMapper {

    public StopJpaEntity toEntity(Stop stop) {
        return new StopJpaEntity(
                stop.getId().value(),
                stop.getName(),
                stop.getLatitude(),
                stop.getLongitude()
        );
    }

    public Stop toDomain(StopJpaEntity entity) {
        return Stop.reconstitute(
                new StopId(entity.getId()),
                entity.getName(),
                entity.getLatitude(),
                entity.getLongitude()
        );
    }
}