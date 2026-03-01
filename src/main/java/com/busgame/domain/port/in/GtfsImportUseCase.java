// domain/port/in/GtfsImportUseCase.java
package com.busgame.domain.port.in;

import com.busgame.application.gtfs.GtfsImportReport;
import java.io.InputStream;

public interface GtfsImportUseCase {
    /**
     * Importe un fichier GTFS complet (.zip).
     * Retourne un rapport indiquant ce qui a ete importe
     * et les erreurs eventuelles.
     */
    GtfsImportReport importGtfs(InputStream zipStream);
}