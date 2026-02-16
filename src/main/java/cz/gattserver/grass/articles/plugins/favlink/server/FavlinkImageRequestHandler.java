package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.articles.plugins.favlink.config.FavlinkConfiguration;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Component
public class FavlinkImageRequestHandler extends AbstractGrassRequestHandler {

    private final ConfigurationService configurationService;
    private final FileSystemService fileSystemService;

    public FavlinkImageRequestHandler(ConfigurationService configurationService, FileSystemService fileSystemService) {
        this.configurationService = configurationService;
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        FavlinkConfiguration configuration = new FavlinkConfiguration();
        configurationService.loadConfiguration(configuration);
        return fileSystemService.getFileSystem().getPath(configuration.getOutputPath(), fileName);
    }
}