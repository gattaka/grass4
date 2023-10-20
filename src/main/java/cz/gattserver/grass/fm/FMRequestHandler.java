package cz.gattserver.grass.fm;


import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;
import cz.gattserver.grass.fm.config.FMConfiguration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/" + FMConfiguration.FM_PATH + "/*")
public class FMRequestHandler extends AbstractConfiguratedPathRequestHandler {

    private static final long serialVersionUID = 7154339775034959876L;

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        FMConfiguration configuration = new FMConfiguration();
        getConfigurationService().loadConfiguration(configuration);
        return getFileSystemService().getFileSystem().getPath(configuration.getRootDir(), fileName);
    }

}
