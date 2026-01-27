package cz.gattserver.grass.pg;

import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.grass.pg.config.PGConfiguration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/" + PGConfiguration.PG_PATH + "/*")
public class PGRequestHandler extends AbstractConfiguratedPathRequestHandler {

    private static final long serialVersionUID = 7154339775034959876L;

    @Override
    protected Path getPath(String fileName, HttpServletRequest httpRequest) throws FileNotFoundException {
        PGConfiguration configuration = new PGConfiguration();
        getConfigurationService().loadConfiguration(configuration);
        Path path = getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
        if (!path.toAbsolutePath().startsWith(configuration.getRootDir()))
            throw new IllegalArgumentException("Podtečení cesty");
        return path;
    }
}
