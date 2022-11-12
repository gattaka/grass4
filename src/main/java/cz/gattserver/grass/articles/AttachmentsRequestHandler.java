package cz.gattserver.grass.articles;

import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.common.spring.SpringContextHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/" + ArticlesConfiguration.ATTACHMENTS_PATH + "/*")
public class AttachmentsRequestHandler extends AbstractConfiguratedPathRequestHandler {

	private static final long serialVersionUID = 7154339775034959876L;

	@Autowired
	private ArticleService articleService;

	@Override
	protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
		if (!fileName.matches("/[0-9]+/[^/]+"))
			throw new GrassPageException(400);
		String[] chunks = fileName.split("/");
		String id = chunks[1];
		String name = chunks[2];
		return articleService.getAttachmentFilePath(id, name);
	}

	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}

}
