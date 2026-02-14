package cz.gattserver.grass.campgames;

import java.io.FileNotFoundException;
import java.nio.file.Path;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;

import cz.gattserver.grass.campgames.service.CampgamesService;
import cz.gattserver.grass.core.server.AbstractConfiguratedPathRequestHandler;
import org.springframework.beans.factory.annotation.Autowired;

import cz.gattserver.common.spring.SpringContextHelper;

@WebServlet(urlPatterns = "/" + CampgamesConfiguration.CAMPGAMES_PATH + "/*")
public class CampgamesRequestHandler extends AbstractConfiguratedPathRequestHandler {

    @Autowired
    private CampgamesService campgamesService;

    @Override
    protected Path getPath(String fileName, HttpServletRequest request) throws FileNotFoundException {
        if (!fileName.matches("/[0-9]+/[^/]+"))
            throw new FileNotFoundException();
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
