package pe.gob.minsa.indicadores.infraestructure.controller;

import pe.gob.minsa.indicadores.domain.ports.in.TableUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/operaciones")
@Tag(name = "Gestión de Tablas")
@RequiredArgsConstructor
public class TableController {

	@Autowired
    private TableUseCase tableUseCase;

	@PostMapping(value = "/carga/tabla", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Subir CSV (Max 100MB)")
    public ResponseEntity<Void> uploadCsv(
    	    @Parameter(description = "Archivo CSV (hasta 100MB)") 
    	    @RequestPart("file") MultipartFile file    		
    		) throws IOException {
    	System.out.println("Se ha invocado el método...");
        tableUseCase.createTableFromCsv(file);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/mantenimiento/tabla")
    @Operation(summary = "Listar todas las tablas")
    public ResponseEntity<List<String>> listTables() {
        return ResponseEntity.ok(tableUseCase.listAllTables());
    }

    @DeleteMapping("/mantenimiento/tabla/{nombreTabla}")
    @Operation(summary = "Eliminar tabla")
    public ResponseEntity<Void> deleteTable(@PathVariable String nombreTabla) {
        tableUseCase.deleteTable(nombreTabla);
        return ResponseEntity.noContent().build();
    }
}