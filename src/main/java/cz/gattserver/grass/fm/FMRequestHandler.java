package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.FileSystemService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.Serial;
import java.nio.file.Path;

@Component
public class FMRequestHandler extends AbstractGrassRequestHandler {

    @Serial
    private static final long serialVersionUID = -5628987113125262177L;

    private final FileSystemService fileSystemService;

    @Value("${fm.root.path}")
    private String rootPath;

    public FMRequestHandler(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        return fileSystemService.getFileSystem().getPath(rootPath, fileName);
    }
}