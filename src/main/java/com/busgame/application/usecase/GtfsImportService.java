// application/usecase/GtfsImportService.java
package com.busgame.application.usecase;

import com.busgame.application.gtfs.GtfsCsvParser;
import com.busgame.application.gtfs.GtfsImportReport;
import com.busgame.domain.model.*;
import com.busgame.domain.model.Calendar;
import com.busgame.domain.port.in.GtfsImportUseCase;
import com.busgame.domain.port.out.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Importe un fichier GTFS complet dans le jeu.
 *
 * Ordre d'import obligatoire (contraintes de cle etrangere) :
 * 1. stops.txt
 * 2. routes.txt
 * 3. calendar.txt
 * 4. calendar_dates.txt
 * 5. trips.txt
 * 6. stop_times.txt
 *
 * Chaque fichier est traite independamment.
 * Une erreur sur une ligne n'interrompt pas le traitement
 * des lignes suivantes.
 */
@Service
public class GtfsImportService implements GtfsImportUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(GtfsImportService.class);

    private static final DateTimeFormatter GTFS_DATE =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private static final DateTimeFormatter GTFS_TIME =
            DateTimeFormatter.ofPattern("H:mm:ss");

    private final StopRepository        stopRepository;
    private final RouteRepository       routeRepository;
    private final CalendarRepository    calendarRepository;
    private final CalendarDateRepository calendarDateRepository;
    private final TripRepository        tripRepository;

    public GtfsImportService(
            StopRepository stopRepository,
            RouteRepository routeRepository,
            CalendarRepository calendarRepository,
            CalendarDateRepository calendarDateRepository,
            TripRepository tripRepository) {
        this.stopRepository         = stopRepository;
        this.routeRepository        = routeRepository;
        this.calendarRepository     = calendarRepository;
        this.calendarDateRepository = calendarDateRepository;
        this.tripRepository         = tripRepository;
    }

    @Override
    @Transactional
    public GtfsImportReport importGtfs(InputStream zipStream) {
        GtfsImportReport report = new GtfsImportReport();

        // Extraire tous les fichiers du zip en memoire
        // (les fichiers GTFS sont generalement petits)
        Map<String, byte[]> files = extractZip(zipStream, report);
        if (files.isEmpty()) return report;

        // Tables de correspondance GTFS id -> notre UUID
        // Necessaires pour resoudre les references croisees
        Map<String, StopId>     stopIndex     = new HashMap<>();
        Map<String, RouteId>    routeIndex    = new HashMap<>();
        Map<String, ServiceId>  serviceIndex  = new HashMap<>();

        // Import dans l'ordre des dependances
        if (files.containsKey("stops.txt"))
            importStops(files.get("stops.txt"), stopIndex, report);

        if (files.containsKey("routes.txt"))
            importRoutes(files.get("routes.txt"), routeIndex, report);

        if (files.containsKey("calendar.txt"))
            importCalendars(files.get("calendar.txt"),
                    serviceIndex, report);

        if (files.containsKey("calendar_dates.txt"))
            importCalendarDates(files.get("calendar_dates.txt"),
                    serviceIndex, report);

        if (files.containsKey("trips.txt"))
            importTrips(files.get("trips.txt"),
                    routeIndex, serviceIndex, report);

        if (files.containsKey("stop_times.txt"))
            importStopTimes(files.get("stop_times.txt"),
                    stopIndex, report);

        log.info(report.toString());
        return report;
    }

    // ── Extraction du ZIP ─────────────────────────────────────────────

    private Map<String, byte[]> extractZip(InputStream zipStream,
                                           GtfsImportReport report) {
        Map<String, byte[]> files = new HashMap<>();
        try (ZipInputStream zip = new ZipInputStream(zipStream)) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    String name = new File(entry.getName()).getName();
                    files.put(name, zip.readAllBytes());
                }
                zip.closeEntry();
            }
        } catch (IOException e) {
            report.addError("Impossible de lire le fichier ZIP : "
                    + e.getMessage());
        }
        return files;
    }

    // ── Import stops.txt ──────────────────────────────────────────────

    private void importStops(byte[] data,
                             Map<String, StopId> stopIndex,
                             GtfsImportReport report) {
        try {
            List<Map<String, String>> rows =
                    GtfsCsvParser.parse(new ByteArrayInputStream(data));

            for (Map<String, String> row : rows) {
                try {
                    String gtfsId = required(row, "stop_id");
                    String name   = required(row, "stop_name");
                    double lat    = parseDouble(row, "stop_lat", 0.0);
                    double lon    = parseDouble(row, "stop_lon", 0.0);

                    Stop stop = Stop.create(name, lat, lon);
                    stopRepository.save(stop);
                    stopIndex.put(gtfsId, stop.getId());
                    report.incrementStops();

                } catch (Exception e) {
                    report.addError("stops.txt ligne ignoree : "
                            + e.getMessage());
                }
            }
        } catch (IOException e) {
            report.addError("Erreur lecture stops.txt : " + e.getMessage());
        }
    }

    // ── Import routes.txt ─────────────────────────────────────────────

    private void importRoutes(byte[] data,
                              Map<String, RouteId> routeIndex,
                              GtfsImportReport report) {
        try {
            List<Map<String, String>> rows =
                    GtfsCsvParser.parse(new ByteArrayInputStream(data));

            for (Map<String, String> row : rows) {
                try {
                    String gtfsId   = required(row, "route_id");
                    String shortName = row.getOrDefault(
                            "route_short_name", "");
                    String longName  = row.getOrDefault(
                            "route_long_name", shortName);

                    Route route = Route.create(
                            longName.isBlank() ? shortName : longName,
                            "",
                            shortName);
                    routeRepository.save(route);
                    routeIndex.put(gtfsId, route.getId());
                    report.incrementRoutes();

                } catch (Exception e) {
                    report.addError("routes.txt ligne ignoree : "
                            + e.getMessage());
                }
            }
        } catch (IOException e) {
            report.addError("Erreur lecture routes.txt : "
                    + e.getMessage());
        }
    }

    // ── Import calendar.txt ───────────────────────────────────────────

    private void importCalendars(byte[] data,
                                 Map<String, ServiceId> serviceIndex,
                                 GtfsImportReport report) {
        try {
            List<Map<String, String>> rows =
                    GtfsCsvParser.parse(new ByteArrayInputStream(data));

            for (Map<String, String> row : rows) {
                try {
                    String gtfsId = required(row, "service_id");

                    Set<DayOfWeek> activeDays = new HashSet<>();
                    if ("1".equals(row.get("monday")))
                        activeDays.add(DayOfWeek.MONDAY);
                    if ("1".equals(row.get("tuesday")))
                        activeDays.add(DayOfWeek.TUESDAY);
                    if ("1".equals(row.get("wednesday")))
                        activeDays.add(DayOfWeek.WEDNESDAY);
                    if ("1".equals(row.get("thursday")))
                        activeDays.add(DayOfWeek.THURSDAY);
                    if ("1".equals(row.get("friday")))
                        activeDays.add(DayOfWeek.FRIDAY);
                    if ("1".equals(row.get("saturday")))
                        activeDays.add(DayOfWeek.SATURDAY);
                    if ("1".equals(row.get("sunday")))
                        activeDays.add(DayOfWeek.SUNDAY);

                    if (activeDays.isEmpty()) {
                        report.addError(
                                "calendar.txt : service " + gtfsId
                                        + " sans jour actif — ignore.");
                        continue;
                    }

                    LocalDate startDate = LocalDate.parse(
                            required(row, "start_date"), GTFS_DATE);
                    LocalDate endDate = LocalDate.parse(
                            required(row, "end_date"), GTFS_DATE);

                    Calendar calendar = Calendar.create(
                            activeDays, startDate, endDate);
                    calendarRepository.save(calendar);
                    serviceIndex.put(gtfsId, calendar.getId());
                    report.incrementCalendars();

                } catch (Exception e) {
                    report.addError("calendar.txt ligne ignoree : "
                            + e.getMessage());
                }
            }
        } catch (IOException e) {
            report.addError("Erreur lecture calendar.txt : "
                    + e.getMessage());
        }
    }

    // ── Import calendar_dates.txt ─────────────────────────────────────

    private void importCalendarDates(byte[] data,
                                     Map<String, ServiceId> serviceIndex,
                                     GtfsImportReport report) {
        try {
            List<Map<String, String>> rows =
                    GtfsCsvParser.parse(new ByteArrayInputStream(data));

            for (Map<String, String> row : rows) {
                try {
                    String gtfsServiceId = required(row, "service_id");
                    ServiceId serviceId  = serviceIndex.get(gtfsServiceId);

                    if (serviceId == null) {
                        report.addError(
                                "calendar_dates.txt : service_id "
                                        + gtfsServiceId + " inconnu — ignore.");
                        continue;
                    }

                    LocalDate date = LocalDate.parse(
                            required(row, "date"), GTFS_DATE);
                    int exceptionCode = Integer.parseInt(
                            required(row, "exception_type"));
                    CalendarDateException exceptionType =
                            exceptionCode == 1
                                    ? CalendarDateException.ADDED
                                    : CalendarDateException.REMOVED;

                    CalendarDate cd = CalendarDate.create(
                            serviceId, date, exceptionType);
                    calendarDateRepository.save(cd);

                } catch (Exception e) {
                    report.addError("calendar_dates.txt ligne ignoree : "
                            + e.getMessage());
                }
            }
        } catch (IOException e) {
            report.addError("Erreur lecture calendar_dates.txt : "
                    + e.getMessage());
        }
    }

    // ── Import trips.txt ──────────────────────────────────────────────

    private void importTrips(byte[] data,
                             Map<String, RouteId> routeIndex,
                             Map<String, ServiceId> serviceIndex,
                             GtfsImportReport report) {
        try {
            List<Map<String, String>> rows =
                    GtfsCsvParser.parse(new ByteArrayInputStream(data));

            // Index trip_id GTFS -> notre TripId
            // Necessaire pour stop_times.txt
            Map<String, TripId> tripIndex = new HashMap<>();

            for (Map<String, String> row : rows) {
                try {
                    String gtfsTripId    = required(row, "trip_id");
                    String gtfsRouteId   = required(row, "route_id");
                    String gtfsServiceId = required(row, "service_id");

                    RouteId routeId = routeIndex.get(gtfsRouteId);
                    if (routeId == null) {
                        report.addError(
                                "trips.txt : route_id " + gtfsRouteId
                                        + " inconnu pour trip " + gtfsTripId
                                        + " — ignore.");
                        continue;
                    }

                    ServiceId serviceId = serviceIndex.get(gtfsServiceId);
                    if (serviceId == null) {
                        report.addError(
                                "trips.txt : service_id " + gtfsServiceId
                                        + " inconnu pour trip " + gtfsTripId
                                        + " — ignore.");
                        continue;
                    }

                    String headsign = row.getOrDefault(
                            "trip_headsign", gtfsTripId);
                    int dirCode = parseInt(row, "direction_id", 0);
                    DirectionId direction = dirCode == 1
                            ? DirectionId.INBOUND
                            : DirectionId.OUTBOUND;

                    Trip trip = Trip.create(
                            routeId, serviceId, direction, headsign);
                    tripRepository.save(trip);

                    // Stocker dans l'index pour stop_times
                    tripIndex.put(gtfsTripId, trip.getId());
                    report.incrementTrips();

                } catch (Exception e) {
                    report.addError("trips.txt ligne ignoree : "
                            + e.getMessage());
                }
            }

            // Stocker l'index dans le contexte pour stop_times
            // On passe par un champ temporaire de la session d'import
            this.currentTripIndex = tripIndex;

        } catch (IOException e) {
            report.addError("Erreur lecture trips.txt : " + e.getMessage());
        }
    }

    // Index temporaire trip GTFS id -> TripId interne
    // Valide uniquement pendant un import
    private Map<String, TripId> currentTripIndex = new HashMap<>();

    // ── Import stop_times.txt ─────────────────────────────────────────

    private void importStopTimes(byte[] data,
                                 Map<String, StopId> stopIndex,
                                 GtfsImportReport report) {
        try {
            List<Map<String, String>> rows =
                    GtfsCsvParser.parse(new ByteArrayInputStream(data));

            // Grouper par trip_id pour reconstruire chaque Trip
            Map<String, List<Map<String, String>>> byTrip =
                    new LinkedHashMap<>();
            for (Map<String, String> row : rows) {
                byTrip.computeIfAbsent(
                        row.get("trip_id"),
                        k -> new ArrayList<>()).add(row);
            }

            for (Map.Entry<String, List<Map<String, String>>> entry
                    : byTrip.entrySet()) {

                String gtfsTripId = entry.getKey();
                TripId tripId = currentTripIndex.get(gtfsTripId);

                if (tripId == null) {
                    report.addError(
                            "stop_times.txt : trip_id " + gtfsTripId
                                    + " inconnu — ignore.");
                    continue;
                }

                Trip trip = tripRepository.findById(tripId).orElse(null);
                if (trip == null) continue;

                // Trier par stop_sequence avant d'ajouter
                List<Map<String, String>> sortedRows = entry.getValue()
                        .stream()
                        .sorted(Comparator.comparingInt(
                                r -> parseInt(r, "stop_sequence", 0)))
                        .toList();

                double cumulativeDistance = 0;

                for (int i = 0; i < sortedRows.size(); i++) {
                    Map<String, String> row = sortedRows.get(i);
                    try {
                        String gtfsStopId = required(row, "stop_id");
                        StopId stopId = stopIndex.get(gtfsStopId);

                        if (stopId == null) {
                            report.addError(
                                    "stop_times.txt : stop_id " + gtfsStopId
                                            + " inconnu — ignore.");
                            continue;
                        }

                        LocalTime arrival   = parseGtfsTime(
                                row.get("arrival_time"));
                        LocalTime departure = parseGtfsTime(
                                row.get("departure_time"));
                        Distance distance   = new Distance(
                                i == 0 ? 0 : 0.5); // Distance par defaut

                        trip.addStopTime(stopId, arrival,
                                departure, distance);
                        report.incrementStopTimes();

                    } catch (Exception e) {
                        report.addError(
                                "stop_times.txt trip " + gtfsTripId
                                        + " ligne ignoree : " + e.getMessage());
                    }
                }

                tripRepository.save(trip);
            }

        } catch (IOException e) {
            report.addError("Erreur lecture stop_times.txt : "
                    + e.getMessage());
        }
    }

    // ── Utilitaires ───────────────────────────────────────────────────

    private String required(Map<String, String> row, String key) {
        String value = row.get(key);
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(
                    "Champ obligatoire manquant : " + key);
        return value;
    }

    private double parseDouble(Map<String, String> row,
                               String key, double defaultValue) {
        try {
            String v = row.get(key);
            return (v != null && !v.isBlank())
                    ? Double.parseDouble(v) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseInt(Map<String, String> row,
                         String key, int defaultValue) {
        try {
            String v = row.get(key);
            return (v != null && !v.isBlank())
                    ? Integer.parseInt(v) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseInt(Map<String, String> row, String key) {
        return parseInt(row, key, 0);
    }

    /**
     * GTFS autorise des heures > 23 pour les services apres minuit.
     * Ex: "25:30:00" = 01:30 le lendemain.
     * On ramene au-dela de 24h dans la plage 0-23.
     */
    private LocalTime parseGtfsTime(String value) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException("Heure GTFS manquante.");

        String[] parts = value.trim().split(":");
        int hours   = Integer.parseInt(parts[0]) % 24;
        int minutes = Integer.parseInt(parts[1]);
        int seconds = Integer.parseInt(parts[2]);
        return LocalTime.of(hours, minutes, seconds);
    }
}