package cz.gattserver.grass.articles.plugins.favlink.server;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Component
public class FavlinkImageRequestHandler extends AbstractGrassRequestHandler {

    private final ConfigurationService configurationService;
    private final FileSystemService fileSystemService;

    @Value("${favlink.root.path}")
    private String favlinkRootPath;

    public FavlinkImageRequestHandler(ConfigurationService configurationService, FileSystemService fileSystemService) {
        this.configurationService = configurationService;
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        return fileSystemService.getFileSystem().getPath(favlinkRootPath, fileName);
    }
}