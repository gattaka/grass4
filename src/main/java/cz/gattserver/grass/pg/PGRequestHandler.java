package cz.gattserver.grass.pg;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.FileSystemService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.Serial;
import java.nio.file.Path;

@Component
public class PGRequestHandler extends AbstractGrassRequestHandler {

    @Serial
    private static final long serialVersionUID = 6875894210256634225L;

    private final FileSystemService fileSystemService;

    @Value("${pg.root.path}")
    private String rootPath;


    public PGRequestHandler(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest httpRequest) throws FileNotFoundException {
        Path path = fileSystemService.getFileSystem().getPath(rootPath, fileName);
        if (!path.toAbsolutePath().startsWith(rootPath))
            throw new IllegalArgumentException("Podtečení cesty");
        return path;
    }
}