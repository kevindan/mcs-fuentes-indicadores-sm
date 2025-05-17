package pe.gob.minsa.indicadores.infraestructure.batch;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import pe.gob.minsa.indicadores.domain.model.DataType;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CsvProcessor {

	public static List<Map<String, String>> process(InputStream csvStream) throws IOException {
	    try (Reader reader = new InputStreamReader(new BOMInputStream(csvStream));
	         CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {

	        return parser.getRecords().stream()
	            .map(record -> {
	                Map<String, String> row = new LinkedHashMap<>();
	                parser.getHeaderNames().forEach(header -> 
	                    row.put(header, record.get(header)));
	                return row;
	            })
	            .collect(Collectors.toList());
	    }
	}
    
    private static String sanitizeColumnName(String rawName) {
        // Eliminar BOM, caracteres especiales y espacios
        return rawName.replaceAll("\\p{C}", "") // Remover caracteres de control Unicode
            .replaceAll("[^a-zA-Z0-9_]", "_")   // Reemplazar caracteres inválidos
            .trim();
    }
    
    // Nuevo método para inferir tipo de dato de una columna
    public static DataType inferDataType(List<Map<String, String>> data, String columnName) {
        return data.stream()
            .map(row -> row.get(columnName))
            .filter(Objects::nonNull)
            .findFirst()
            .map(value -> {
                if (value.matches("^\\d+$")) return DataType.INT;
                if (value.matches("^\\d+\\.\\d+$")) return DataType.DECIMAL;
                if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) return DataType.DATE;
                return DataType.VARCHAR;
            })
            .orElse(DataType.VARCHAR); // Default
    }
    
    public static Map<String, DataType> inferColumnTypes(List<Map<String, String>> data) {
        Map<String, DataType> types = new HashMap<>();
        
        if (!data.isEmpty()) {
            data.get(0).keySet().forEach(column -> {
                // Lógica mejorada de inferencia de tipos
                String sampleValue = data.get(0).get(column);
                DataType detectedType = detectDataType(sampleValue);
                types.put(column, detectedType);
            });
        }
        
        return types;
    }

    private static DataType detectDataType(String value) {
        if (value == null) return DataType.VARCHAR;
        
        if (value.matches("^\\d+$")) return DataType.INT;
        if (value.matches("^\\d+\\.\\d+$")) return DataType.DECIMAL;
        if (value.matches("^\\d{4}-\\d{2}-\\d{2}$")) return DataType.DATE;
        
        return DataType.VARCHAR;
    }
    
    
}