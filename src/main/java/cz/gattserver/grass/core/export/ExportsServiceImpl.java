package cz.gattserver.grass.core.export;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import cz.gattserver.grass.core.exception.GrassException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.repo.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;

@Service
public class ExportsServiceImpl implements ExportsService {

	private static Logger logger = LoggerFactory.getLogger(ExportsServiceImpl.class);

	@Override
	public Path createPDFReport(
			JRDataSource jrDataSource, Map<String, Object> params, String reportFileName,
			ExportType type) {
		try {
			Path tmpPath = Files.createTempFile("grass-jasper-", ".pdf");
			OutputStream fileOutputStream = Files.newOutputStream(tmpPath);

			String basepath = "META-INF/resources/jasper";
			String path = basepath + reportFileName + ".jasper";
			InputStream jasperReportStream = getClass().getClassLoader().getResourceAsStream(path);

			logger.info("Hledám " + path + ", výsledek: " + jasperReportStream);

			params.put("SUBREPORT_DIR", path);

			SimpleJasperReportsContext jasperReportsContext = new SimpleJasperReportsContext();

			// https://stackoverflow.com/questions/1771679/difference-between-threads-context-class-loader-and-normal-classloader
			// Protože Jasper používá Thread.currentThread().getContextClassLoader(), což nefunguje pod spring boot s
			// embedded Tomcat, je potřeba to přepsat fungujícím getClass().getClassLoader()
			CustomJasperRepositoryService repositoryService =
					new CustomJasperRepositoryService(DefaultJasperReportsContext.getInstance());
			jasperReportsContext.setExtensions(RepositoryService.class, Arrays.asList(repositoryService));

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
