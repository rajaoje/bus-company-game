package com.busgame.infrastructure.web;

import java.util.List;

// infrastructure/web/GtfsImportReportResponse.java
public record GtfsImportReportResponse(
        int stopsImported,
        int routesImported,
        int calendarsImported,
        int tripsImported,
        int stopTimesImported,
        List<String> errors
) {}
