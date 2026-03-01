package com.busgame.infrastructure.web;

import java.time.LocalDate;

// infrastructure/web/GenerationReportResponse.java
public record GenerationReportResponse(
        LocalDate date,
        int schedulesGenerated
) {}
