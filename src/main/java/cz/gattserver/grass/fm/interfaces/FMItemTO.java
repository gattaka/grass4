package cz.gattserver.grass.fm.interfaces;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public record FMItemTO(String name, String pathFromFMRoot, boolean directory, Long numericSize, String size,
                       LocalDateTime lastModified) implements Serializable {

    @Serial
    private static final long serialVersionUID = 44677581670349098L;

    public FMItemTO(String name, String pathFromFMRoot) {
        this(name, pathFromFMRoot, false, null, null, null);
    }

    public FMItemTO(String name, boolean directory, Long numericSize, String size, LocalDateTime lastModified) {
        this(name, null, directory, numericSize, size, lastModified);
    }

}
