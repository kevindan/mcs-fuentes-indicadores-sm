package pe.gob.minsa.indicadores.application.utils;

import pe.gob.minsa.indicadores.domain.model.DataType;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class DataTypeInferenceEngine {

    private static final Pattern INTEGER_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern DECIMAL_PATTERN = Pattern.compile("^\\d+(\\.\\d+)?$");
    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,          // yyyy-MM-dd
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy"),
        DateTimeFormatter.ofPattern("yyyyMMdd")
    );

    public Map<String, DataType> inferColumnTypes(List<Map<String, String>> data) {
        Map<String, String> sampleRow = data.get(0);
        return sampleRow.keySet().stream()
            .collect(Collectors.toMap(
                Function.identity(),
                column -> inferTypeForColumn(data, column)
            ));
    }

    private DataType inferTypeForColumn(List<Map<String, String>> data, String column) {
        List<String> values = data.stream()
            .map(row -> row.get(column))
            .filter(value -> value != null && !value.isEmpty())
            .limit(1000) // Muestra de 1000 valores para inferencia
            .toList();

        if (values.isEmpty()) return DataType.VARCHAR;

        boolean allIntegers = values.stream().allMatch(v -> INTEGER_PATTERN.matcher(v).matches());
        if (allIntegers) return DataType.INT;

        boolean allDecimals = values.stream().allMatch(v -> DECIMAL_PATTERN.matcher(v).matches());
        if (allDecimals) return DataType.DECIMAL;

        boolean allDates = values.stream().allMatch(this::isValidDate);
        if (allDates) return DataType.DATE;

        return DataType.VARCHAR;
    }

    private boolean isValidDate(String value) {
        return DATE_FORMATTERS.stream().anyMatch(formatter -> {
            try {
                formatter.parse(value);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        });
    }
}