package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;
import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.nio.file.Path;

@WebServlet(urlPatterns = "/" + FavlinkConfiguration.IMAGE_PATH_ALIAS + "/*")
public class FavlinkImageRequestHandler extends AbstractConfiguratedPathRequestHandler {

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        FavlinkConfiguration configuration = new FavlinkConfiguration();
        getConfigurationService().loadConfiguration(configuration);
        return getFileSystemService().getFileSystem().getPath(configuration.getOutputPath(), fileName);
    }

}
