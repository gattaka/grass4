package cz.gattserver.grass.core.export;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import cz.gattserver.grass.core.exception.GrassException;
import lombok.extern.slf4j.Slf4j;
import org.openpdf.text.pdf.BaseFont;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

@Slf4j
@Service
public class ExportsServiceImpl implements ExportsService {

    @Override
    public Path createPDFReport(Context ctx, String reportFileName) {
        try {
            // 1. Set up Thymeleaf
            var resolver = new ClassLoaderTemplateResolver();
            resolver.setPrefix("META-INF/resources/templates/");
            resolver.setSuffix(".html");
            resolver.setCharacterEncoding("UTF-8");

            var engine = new TemplateEngine();
            engine.setTemplateResolver(resolver);

            String html = engine.process(reportFileName, ctx);

            Path outFile = Files.createTempFile("report", ".pdf");
            try (var os = Files.newOutputStream(outFile)) {
                var renderer = new ITextRenderer();

                String prefix = "/META-INF/resources/fonts/";

                var fontResolver = renderer.getFontResolver();
                fontResolver.addFont(getClass().getResource(prefix + "DejaVuSans.ttf").toString(), BaseFont.IDENTITY_H,
                        BaseFont.EMBEDDED);
                fontResolver.addFont(getClass().getResource(prefix + "DejaVuSans-Bold.ttf").toString(),
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                fontResolver.addFont(getClass().getResource(prefix + "DejaVuSansMono.ttf").toString(),
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                fontResolver.addFont(getClass().getResource(prefix + "DejaVuSansMono-Bold.ttf").toString(),
                        BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

                renderer.setDocumentFromString(html);
                renderer.layout();
                renderer.createPDF(os);
            }

            return outFile;
        } catch (Exception e) {
            throw new GrassException("Export se nezdařil", e);
        }
    }
}