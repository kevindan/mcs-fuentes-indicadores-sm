package pe.gob.minsa.indicadores.application.service;

import pe.gob.minsa.indicadores.domain.ports.in.TableUseCase;
import pe.gob.minsa.indicadores.domain.ports.out.TableRepositoryPort;
import pe.gob.minsa.indicadores.infraestructure.batch.CsvProcessor;
import pe.gob.minsa.indicadores.application.utils.DataTypeInferenceEngine;
import pe.gob.minsa.indicadores.domain.model.DataType;

import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements TableUseCase {

	@Autowired
    private TableRepositoryPort repository;
    @Autowired
	private DataTypeInferenceEngine typeInference;

    @Override
    public void createTableFromCsv(MultipartFile file) throws IOException {
        String tableName = sanitizeTableName(file.getOriginalFilename());
        List<Map<String, String>> data = CsvProcessor.process(file.getInputStream());
        
        Map<String, DataType> columnTypes = typeInference.inferColumnTypes(data);
        repository.createTable(tableName, columnTypes);
        repository.bulkInsert(tableName, data);
    }

    private String sanitizeTableName(String rawName) {
        // Paso 1: Remover extensión y caracteres inválidos
        String sanitized = FilenameUtils.removeExtension(rawName)
            .replaceAll("[^a-zA-Z0-9_]", "_");
        
        // Paso 2: Asegurar longitud máxima usando sanitized.length()
        int maxLength = Math.min(sanitized.length(), 128);
        sanitized = sanitized.substring(0, maxLength);
        
        // Paso 3: Si empieza con número, agregar prefijo
        if (sanitized.matches("^\\d.*")) {
            sanitized = "tbl_" + sanitized;
            // Asegurar nuevamente la longitud máxima
            sanitized = sanitized.substring(0, Math.min(sanitized.length(), 128));
        }
        
        // Paso 4: Validar nombre no vacío
        if (sanitized.isEmpty()) {
            throw new IllegalArgumentException("Nombre de tabla inválido");
        }
        
        return sanitized;
    }

    @Override
    public List<String> listAllTables() {
        return repository.getAllTables();
    }

    @Override
    public List<String> getTableColumns(String tableName) {
        return repository.getTableColumns(tableName);
    }

    @Override
    public void deleteTable(String tableName) {
        repository.dropTable(tableName);
    }
}