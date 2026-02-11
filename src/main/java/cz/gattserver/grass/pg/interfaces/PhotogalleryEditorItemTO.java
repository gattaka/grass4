package cz.gattserver.grass.pg.interfaces;

import java.nio.file.Path;

public class PhotogalleryEditorItemTO  {

    private String name;
    private Path path;

    public PhotogalleryEditorItemTO(String name, Path path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}