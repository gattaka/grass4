package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.fm.config.FMConfiguration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

    private final ConfigurationService configurationService;
    private final FileSystemService fileSystemService;

    public FMRequestHandler(ConfigurationService configurationService, FileSystemService fileSystemService) {
        this.configurationService = configurationService;
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        FMConfiguration configuration = new FMConfiguration();
        configurationService.loadConfiguration(configuration);
        return fileSystemService.getFileSystem().getPath(configuration.getRootDir(), fileName);
    }
}