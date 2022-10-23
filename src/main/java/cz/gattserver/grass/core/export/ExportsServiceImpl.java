package cz.gattserver.grass.core.export;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import cz.gattserver.grass.core.exception.GrassException;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReportsContext;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service
public class ExportsServiceImpl implements ExportsService {

	@Override
	public Path createPDFReport(JRDataSource jrDataSource, Map<String, Object> params, String reportFileName,
			ExportType type) {
		try {
			String path = "/jasper/";
			InputStream jasperReportStream = Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path + reportFileName + ".jasper");
			params.put("SUBREPORT_DIR", path);

			Path tmpPath = Files.createTempFile("grass-jasper-", "pdf");
			OutputStream fileOutputStream = Files.newOutputStream(tmpPath);

			JasperReportsContext jasperReportsContext = DefaultJasperReportsContext.getInstance();
			JasperFillManager jasperFillManager = JasperFillManager.getInstance(jasperReportsContext);
			JasperPrint jasperPrint = jasperFillManager.fill(jasperReportStream, params, jrDataSource);

			JRPdfExporter exporter = new JRPdfExporter();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, fileOutputStream);
			if (ExportType.PRINT == type)
				exporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT, "this.print();");

			exporter.exportReport();
			fileOutputStream.close();

			return tmpPath;
		} catch (Exception e) {
			throw new GrassException("Export se nezdařil", e);
		}
	}

}
