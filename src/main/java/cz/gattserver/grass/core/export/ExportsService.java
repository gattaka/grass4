package cz.gattserver.grass.core.export;

import org.thymeleaf.context.Context;

import java.nio.file.Path;

public interface ExportsService {

    Path createPDFReport(Context ctx, String reportFileName);
}
