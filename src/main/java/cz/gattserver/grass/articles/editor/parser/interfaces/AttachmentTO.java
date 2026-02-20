package cz.gattserver.grass.articles.editor.parser.interfaces;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Getter
@Setter
public class AttachmentTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5487862259841530666L;

    private String name;
    private String size;
    private Long numericSize;
    private LocalDateTime lastModified;
    private Path path;
    private boolean draft;

}