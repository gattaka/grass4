package cz.gattserver.grass.pg;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.ConfigurationService;
import cz.gattserver.grass.core.services.FileSystemService;
import cz.gattserver.grass.pg.config.PGConfiguration;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.Serial;
import java.nio.file.Path;

@Component
public class PGRequestHandler extends AbstractGrassRequestHandler {

    @Serial
    private static final long serialVersionUID = 6875894210256634225L;

    private final ConfigurationService configurationService;
    private final FileSystemService fileSystemService;

    public PGRequestHandler(ConfigurationService configurationService, FileSystemService fileSystemService) {
        this.configurationService = configurationService;
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest httpRequest) throws FileNotFoundException {
        PGConfiguration configuration = new PGConfiguration();
        configurationService.loadConfiguration(configuration);
        Path path = fileSystemService.getFileSystem().getPath(configuration.getRootDir(), fileName);
        if (!path.toAbsolutePath().startsWith(configuration.getRootDir()))
            throw new IllegalArgumentException("Podtečení cesty");
        return path;
    }
}