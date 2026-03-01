// application/gtfs/GtfsImportReport.java
package com.busgame.application.gtfs;

import java.util.ArrayList;
import java.util.List;

/**
 * Rapport d'import GTFS.
 * L'import est best-effort : on continue meme si certaines
 * lignes sont malformees, et on rapporte tout a la fin.
 */
public class GtfsImportReport {

    private int stopsImported      = 0;
    private int routesImported     = 0;
    private int calendarsImported  = 0;
    private int tripsImported      = 0;
    private int stopTimesImported  = 0;
    private final List<String> errors = new ArrayList<>();

    public void incrementStops()     { stopsImported++; }
    public void incrementRoutes()    { routesImported++; }
    public void incrementCalendars() { calendarsImported++; }
    public void incrementTrips()     { tripsImported++; }
    public void incrementStopTimes() { stopTimesImported++; }
    public void addError(String error) { errors.add(error); }

    public int getStopsImported()     { return stopsImported; }
    public int getRoutesImported()    { return routesImported; }
    public int getCalendarsImported() { return calendarsImported; }
    public int getTripsImported()     { return tripsImported; }
    public int getStopTimesImported() { return stopTimesImported; }
    public List<String> getErrors()   { return List.copyOf(errors); }
    public boolean hasErrors()        { return !errors.isEmpty(); }

    @Override
    public String toString() {
        return "Import GTFS : stops=%d, routes=%d, calendars=%d, "
                + "trips=%d, stopTimes=%d, erreurs=%d"
                .formatted(stopsImported, routesImported, calendarsImported,
                        tripsImported, stopTimesImported, errors.size());
    }
}