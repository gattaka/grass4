package cz.gattserver.grass.articles;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.C;
import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.server.AbstractGrassRequestHandlerConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.hc.core5.http.HttpStatus;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;

// Registrace servletu přesunuta do cz.gattserver.grass.ServletConfig, protože tranzitivně vznikal problém s vytvářením instance jakarta.persistence.EntityManager
@Component
public class AttachmentsRequestHandler extends AbstractGrassRequestHandler {

    private final ArticleService articleService;

    public AttachmentsRequestHandler(@Lazy ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        if (!fileName.matches("/[0-9]+/[^/]+")) throw new GrassPageException(HttpStatus.SC_CLIENT_ERROR);
        String[] chunks = fileName.split("/");
        Long id = Long.parseLong(chunks[1]);
        String name = chunks[2];
        Path path = articleService.getAttachmentFilePath(id, name);
        if (path == null) throw new GrassPageException(HttpStatus.SC_NOT_FOUND);
        return path;
    }

    @Override
    protected String getMimeType(Path file) {
        String type = super.getMimeType(file);
        return type + "; charset=utf-8";
    }
}