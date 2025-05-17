package pe.gob.minsa.indicadores.infraestructure.repository;

import pe.gob.minsa.indicadores.domain.ports.out.TableRepositoryPort;
import pe.gob.minsa.indicadores.domain.model.DataType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SqlServerTableRepository implements TableRepositoryPort {

	@Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final Map<DataType, String> SQL_TYPE_MAP = Map.of(
        DataType.VARCHAR, "NVARCHAR(255)",
        DataType.INT, "INT",
        DataType.DECIMAL, "DECIMAL(18,2)",
        DataType.DATE, "DATE"
    );

    @Override
    public void createTable(String tableName, Map<String, DataType> columns) {
        String columnsSql = columns.entrySet().stream()
            .map(entry -> entry.getKey() + " " + SQL_TYPE_MAP.get(entry.getValue()))
            .collect(Collectors.joining(", "));
        
        jdbcTemplate.execute("CREATE TABLE " + tableName + " (id INT IDENTITY PRIMARY KEY, " + columnsSql + ")");
    }

    @Override
    public void bulkInsert(String tableName, List<Map<String, String>> data) {
        if (data.isEmpty()) return;
        
        List<String> columns = new ArrayList<>(data.get(0).keySet());
        String sql = buildInsertSql(tableName, columns);
        
        jdbcTemplate.batchUpdate(sql, data.stream()
            .map(row -> columns.stream().map(row::get).toArray())
            .toList());
    }

    private String buildInsertSql(String tableName, List<String> columns) {
        String placeholders = String.join(", ", Collections.nCopies(columns.size(), "?"));
        return "INSERT INTO " + tableName + " (" + String.join(", ", columns) + ") VALUES (" + placeholders + ")";
    }

    @Override
    public List<String> getAllTables() {
        return jdbcTemplate.queryForList(
            "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_TYPE = 'BASE TABLE'", 
            String.class
        );
    }

    @Override
    public List<String> getTableColumns(String tableName) {
        return jdbcTemplate.queryForList(
            "SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ?", 
            String.class, 
            tableName
        );
    }

    @Override
    public void dropTable(String tableName) {
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName);
    }
}