// application/gtfs/GtfsCsvParser.java
package com.busgame.application.gtfs;

import java.io.*;
import java.util.*;

/**
 * Utilitaire de parsing CSV minimaliste pour les fichiers GTFS.
 * GTFS utilise toujours UTF-8 avec une ligne d'en-tete.
 */
public class GtfsCsvParser {

    /**
     * Parse un fichier CSV GTFS en une liste de Maps.
     * Chaque Map represente une ligne : header -> valeur.
     */
    public static List<Map<String, String>> parse(InputStream input)
            throws IOException {
        List<Map<String, String>> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(input, "UTF-8"))) {

            String headerLine = reader.readLine();
            if (headerLine == null) return rows;

            // Supprimer le BOM UTF-8 eventuel
            headerLine = headerLine.replace("\uFEFF", "");
            String[] headers = headerLine.split(",", -1);

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] values = splitCsvLine(line);
                Map<String, String> row = new LinkedHashMap<>();
                for (int i = 0; i < headers.length; i++) {
                    String value = i < values.length
                            ? values[i].trim() : "";
                    row.put(headers[i].trim(), value);
                }
                rows.add(row);
            }
        }
        return rows;
    }

    /**
     * Split respectant les guillemets GTFS.
     * Ex: "stop name, with comma","value" -> 2 elements.
     */
    private static String[] splitCsvLine(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                tokens.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        tokens.add(current.toString());
        return tokens.toArray(new String[0]);
    }
}