package cz.gattserver.grass.campgames;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import cz.gattserver.grass.core.server.AbstractGrassRequestHandler;
import jakarta.servlet.http.HttpServletRequest;

import cz.gattserver.grass.campgames.service.CampgamesService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
public class CampgamesRequestHandler extends AbstractGrassRequestHandler {

    private CampgamesService campgamesService;

    public CampgamesRequestHandler(@Lazy CampgamesService campgamesService) {
        this.campgamesService = campgamesService;
    }

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        if (!fileName.matches("/[0-9]+/[^/]+")) throw new FileNotFoundException();
        String[] chunks = fileName.split("/");
        Long id = Long.parseLong(chunks[1]);
        String name = chunks[2];
        return campgamesService.getCampgameImagesFilePath(id, name);
    }

    @Override
    protected String getMimeType(Path file) {
        return super.getMimeType(file);
    }
}