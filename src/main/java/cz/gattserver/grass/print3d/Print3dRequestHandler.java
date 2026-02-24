package cz.gattserver.grass.print3d;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import cz.gattserver.grass.core.services.FileSystemService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.nio.file.Path;

@Component
public class Print3dRequestHandler extends AbstractGrassRequestHandler {

    private final FileSystemService fileSystemService;

    @Value("${print3d.root.path}")
    private String rootPathName;

    public Print3dRequestHandler(FileSystemService fileSystemService) {
        this.fileSystemService = fileSystemService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        return fileSystemService.getFileSystem().getPath(rootPathName, fileName);
    }
}