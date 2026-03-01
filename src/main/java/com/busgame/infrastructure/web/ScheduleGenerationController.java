package com.busgame.infrastructure.web;

import com.busgame.domain.model.Schedule;
import com.busgame.domain.port.in.ScheduleGenerationUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

// infrastructure/web/ScheduleGenerationController.java
@RestController
@RequestMapping("/api/v1/schedules")
public class ScheduleGenerationController {

    private final ScheduleGenerationUseCase generationUseCase;

    public ScheduleGenerationController(
            ScheduleGenerationUseCase generationUseCase) {
        this.generationUseCase = generationUseCase;
    }

    /**
     * POST /api/v1/schedules/generate?date=2024-09-02
     * Genere automatiquement les schedules pour une date donnee.
     */
    @PostMapping("/generate")
    public ResponseEntity<GenerationReportResponse> generate(
            @RequestParam LocalDate date) {
        List<Schedule> schedules =
                generationUseCase.generateSchedulesForDate(date);
        return ResponseEntity.ok(
                new GenerationReportResponse(
                        date, schedules.size()));
    }
}
