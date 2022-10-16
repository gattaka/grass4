package cz.gattserver.grass.server;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@WebServlet(urlPatterns = "/export/*")
public class ExportRequestHandler extends AbstractGrassRequestHandler {

	private static final long serialVersionUID = -1704579036933184936L;

	public static final String ATTR_PREFIX = "GRASS-JASPER-REPORT-";

	@Override
	protected Path getPath(String fileName) throws FileNotFoundException {
		String[] chunks = fileName.split("/");
		String id = chunks[1];

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
			Object path = request.getSession().getAttribute(ATTR_PREFIX + id);
			if (path != null)
				return (Path) path;
		}

		return null;
	}

	@Override
	protected String getMimeType(Path file) {
		String type = super.getMimeType(file);
		return type + "; charset=utf-8";
	}

}
