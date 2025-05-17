package pe.gob.minsa.indicadores.application.utils;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class DatePatterns {
    public static final List<DateTimeFormatter> FORMATTERS = List.of(
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy"),
        DateTimeFormatter.ofPattern("yyyy/MM/dd")
    );
}