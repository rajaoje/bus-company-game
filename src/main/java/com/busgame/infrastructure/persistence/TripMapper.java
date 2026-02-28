// infrastructure/persistence/TripMapper.java
package com.busgame.infrastructure.persistence;

import com.busgame.domain.model.*;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class TripMapper {

    public TripJpaEntity toEntity(Trip trip) {
        TripJpaEntity entity = new TripJpaEntity(
                trip.getId().value(),
                trip.getRouteId().value(),
                trip.getDirectionId(),
                trip.getHeadsign()
        );

        List<StopTimeJpaEntity> stopTimeEntities = trip.getStopTimes()
                .stream()
                .map(st -> new StopTimeJpaEntity(
                        st.getId().value(),
                        trip.getId().value(),
                        st.getStopId().value(),
                        st.getStopSequence(),
                        st.getArrivalTime(),
                        st.getDepartureTime(),
                        st.getDistanceFromPrevious().kilometers()
                ))
                .toList();

        entity.setStopTimes(stopTimeEntities);
        return entity;
    }

    public Trip toDomain(TripJpaEntity entity) {
        List<StopTime> stopTimes = entity.getStopTimes()
                .stream()
                .sorted(Comparator.comparingInt(
                        StopTimeJpaEntity::getStopSequence))
                .map(st -> StopTime.reconstitute(
                        new StopTimeId(st.getId()),
                        new StopId(st.getStopId()),
                        st.getStopSequence(),
                        st.getArrivalTime(),
                        st.getDepartureTime(),
                        new Distance(st.getDistanceFromPreviousKm())
                ))
                .toList();

        return Trip.reconstitute(
                new TripId(entity.getId()),
                new RouteId(entity.getRouteId()),
                entity.getDirectionId(),
                entity.getHeadsign(),
                stopTimes
        );
    }
}