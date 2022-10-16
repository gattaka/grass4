package cz.gattserver.grass.export;

import java.nio.file.Path;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

public interface ExportsService {

	Path createPDFReport(JRDataSource jrDataSource, Map<String, Object> params, String reportFileName, ExportType type);
}
