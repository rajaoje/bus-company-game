package com.busgame.infrastructure.web;

import com.busgame.application.gtfs.GtfsImportReport;
import com.busgame.domain.port.in.GtfsImportUseCase;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

// infrastructure/web/GtfsController.java
@RestController
@RequestMapping("/api/v1/gtfs")
public class GtfsController {

    private final GtfsImportUseCase gtfsImportUseCase;

    public GtfsController(GtfsImportUseCase gtfsImportUseCase) {
        this.gtfsImportUseCase = gtfsImportUseCase;
    }

    /**
     * POST /api/v1/gtfs/import
     * Accepte un fichier .zip multipart.
     *
     * Exemple curl :
     * curl -X POST /api/v1/gtfs/import \
     *      -F "file=@gtfs.zip"
     */
    @PostMapping(value = "/import",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GtfsImportReportResponse> importGtfs(
            @RequestParam("file") MultipartFile file) {
        try {
            GtfsImportReport report =
                    gtfsImportUseCase.importGtfs(file.getInputStream());
            return ResponseEntity.ok(
                    new GtfsImportReportResponse(
                            report.getStopsImported(),
                            report.getRoutesImported(),
                            report.getCalendarsImported(),
                            report.getTripsImported(),
                            report.getStopTimesImported(),
                            report.getErrors()
                    ));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
