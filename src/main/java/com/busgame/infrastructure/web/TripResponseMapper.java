package com.busgame.infrastructure.web;

import com.busgame.domain.model.Stop;
import com.busgame.domain.model.Trip;
import com.busgame.domain.port.out.StopRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TripResponseMapper {

    private final StopRepository stopRepository;

    public TripResponseMapper(StopRepository stopRepository) {
        this.stopRepository = stopRepository;
    }

    public TripResponse toResponse(Trip trip) {
        List<StopTimeResponse> stopTimeResponses = trip.getStopTimes()
                .stream()
                .map(st -> {
                    // Enrichir avec le nom du stop pour l'affichage
                    String stopName = stopRepository
                            .findById(st.getStopId())
                            .map(Stop::getName)
                            .orElse("Arret inconnu");

                    return new StopTimeResponse(
                            st.getId().value(),
                            st.getStopId().value(),
                            stopName,
                            st.getStopSequence(),
                            st.getArrivalTime(),
                            st.getDepartureTime(),
                            st.getDistanceFromPrevious().kilometers()
                    );
                })
                .toList();

        return new TripResponse(
                trip.getId().value(),
                trip.getRouteId().value(),
                trip.getDirectionId(),
                trip.getHeadsign(),
                stopTimeResponses,
                trip.getTotalDistance().kilometers(),
                trip.getEstimatedDurationMinutes()
        );
    }
}
