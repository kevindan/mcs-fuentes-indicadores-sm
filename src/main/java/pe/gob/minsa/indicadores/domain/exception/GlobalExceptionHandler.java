package pe.gob.minsa.indicadores.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<String> handleDomainException(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.internalServerError().body("Error interno del servidor");
    }
    
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "El archivo excede el tamaño máximo permitido (100MB)");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(response);
    }
    
    @ExceptionHandler({IllegalArgumentException.class, StringIndexOutOfBoundsException.class})
    public ResponseEntity<Map<String, String>> handleInvalidExceptions(Exception ex) {
        Map<String, String> response = new HashMap<>();
        
        String errorMessage = ex instanceof IllegalArgumentException 
            ? "Error en nombres de columnas: " + ex.getMessage()  // Mensaje específico
            : "Nombre de archivo/tabla inválido: " + ex.getMessage();
        
        response.put("error", errorMessage);
        return ResponseEntity.badRequest().body(response);
    }
}