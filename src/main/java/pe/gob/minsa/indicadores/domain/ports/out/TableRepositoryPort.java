package pe.gob.minsa.indicadores.domain.ports.out;

import java.util.List;
import java.util.Map;

import pe.gob.minsa.indicadores.domain.model.DataType;

public interface TableRepositoryPort {

    void createTable(String tableName, Map<String, DataType> columns);
    void bulkInsert(String tableName, List<Map<String, String>> data);
    List<String> getAllTables();
    List<String> getTableColumns(String tableName);
    void dropTable(String tableName);
	
}
