package cz.gattserver.grass.print3d.events;

import cz.gattserver.grass.core.events.ResultEvent;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

public record Print3dZipProcessResultEvent(boolean success, String resultDetails, Throwable resultException,
                                           Path zipFile) implements ResultEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 7585839355976505500L;

    public Print3dZipProcessResultEvent(Path zipFile) {
        this(true, null, null, zipFile);
    }

    public Print3dZipProcessResultEvent(String resultDetails, Throwable exception) {
        this(false, resultDetails, exception, null);
    }
}