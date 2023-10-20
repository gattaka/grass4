package cz.gattserver.grass.core.server;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

@WebServlet(urlPatterns = "/export/*")
public class ExportRequestHandler extends AbstractGrassRequestHandler {

    private static final long serialVersionUID = -1704579036933184936L;

    public static final String ATTR_PREFIX = "GRASS-JASPER-REPORT-";

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        String[] chunks = fileName.split("/");
        String id = chunks[1];
        Object path = request.getSession().getAttribute(ATTR_PREFIX + id);
        if (path != null)
            return (Path) path;
        return null;
    }

    @Override
    protected String getMimeType(Path file) {
        String type = super.getMimeType(file);
        return type + "; charset=utf-8";
    }

}
