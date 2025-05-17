package pe.gob.minsa.indicadores.infraestructure.batch;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CsvProcessor {

    public static List<Map<String, String>> process(InputStream csvStream) throws IOException {
        try (Reader reader = new InputStreamReader(new BOMInputStream(csvStream)); // Manejo de BOM
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

            // 1. Sanitizar encabezados
            List<String> sanitizedHeaders = parser.getHeaderNames().stream()
                .map(CsvProcessor::sanitizeColumnName)
                .collect(Collectors.toList());

            // 2. Procesar registros
            List<Map<String, String>> data = new ArrayList<>();
            for (CSVRecord record : parser) {
                Map<String, String> row = new HashMap<>();
                for (int i = 0; i < sanitizedHeaders.size(); i++) {
                    String header = sanitizedHeaders.get(i);
                    String value = record.get(i); // Obtiene por índice, no por nombre
                    row.put(header, value);
                }
                data.add(row);
            }
            return data;
        }
    }

    private static String sanitizeColumnName(String rawName) {
        // Eliminar BOM, caracteres especiales y espacios
        return rawName.replaceAll("\\p{C}", "") // Remover caracteres de control Unicode
            .replaceAll("[^a-zA-Z0-9_]", "_")   // Reemplazar caracteres inválidos
            .trim();
    }
}