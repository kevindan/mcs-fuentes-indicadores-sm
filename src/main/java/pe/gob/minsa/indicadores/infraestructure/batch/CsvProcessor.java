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
        try (Reader reader = new InputStreamReader(new BOMInputStream(csvStream));
             CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                 .withFirstRecordAsHeader()
                 .withTrim() // Elimina espacios alrededor de los valores
                 .withIgnoreEmptyLines()
             )) {

        //	System.out.println("### Encabezados CSV originales: " + parser.getHeaderNames());
        	
            List<Map<String, String>> data = new ArrayList<>();
            for (CSVRecord record : parser) {
                Map<String, String> row = new LinkedHashMap<>(); // Mantiene orden original
                for (String header : parser.getHeaderNames()) {
                    // Usa el nombre original del encabezado (sin sanitizar)
                    String value = record.get(header); // ← Obtiene por nombre, no por índice
                    row.put(header, value != null ? value.trim() : null);
                    
                    // Log 2: Valores crudos por fila/columna
//                    System.out.println(
//                        String.format("[Fila %d] Columna '%s': Valor = '%s'", 
//                        record.getRecordNumber(), header, value)
//                    );
                    
                    
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