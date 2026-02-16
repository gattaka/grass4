package cz.gattserver.grass.print3d;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.print3d.config.Print3dConfiguration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Component
public class Print3dRequestHandler extends AbstractGrassRequestHandler {

    private final ConfigurationService configurationService;
    private final FileSystemService fileSystemService;

    public Print3dRequestHandler(ConfigurationService configurationService, FileSystemService fileSystemService) {
        this.configurationService = configurationService;
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        Print3dConfiguration configuration = new Print3dConfiguration();
        configurationService.loadConfiguration(configuration);
        return fileSystemService.getFileSystem().getPath(configuration.getRootDir(), fileName);
    }
}