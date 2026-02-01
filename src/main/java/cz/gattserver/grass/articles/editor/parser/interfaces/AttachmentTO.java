package cz.gattserver.grass.articles.editor.parser.interfaces;

import java.nio.file.Path;
import java.time.LocalDateTime;

public class AttachmentTO {

    private String name;
    private String size;
    private Long numericSize;
    private LocalDateTime lastModified;
    private Path path;
    private boolean draft;

    public Path getPath() {
        return path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Long getNumericSize() {
        return numericSize;
    }

    public void setNumericSize(Long numericSize) {
        this.numericSize = numericSize;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    public void setLastModified(LocalDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean isDraft() {
        return draft;
    }

    public void setDraft(boolean draft) {
        this.draft = draft;
    }
}