package pe.gob.minsa.indicadores.domain.ports.in;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface TableUseCase {
	
	void createTableFromCsv(MultipartFile file) throws IOException;

	List<String> listAllTables();

	List<String> getTableColumns(String tableName);

	void deleteTable(String tableName);
}