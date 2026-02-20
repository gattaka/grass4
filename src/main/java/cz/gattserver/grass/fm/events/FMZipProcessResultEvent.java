package cz.gattserver.grass.fm.events;

import cz.gattserver.grass.core.events.ResultEvent;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

public record FMZipProcessResultEvent(boolean success, String resultDetails, Throwable resultException, Path zipFile)
        implements ResultEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 1723470771793883622L;

    public FMZipProcessResultEvent(Path zipFile) {
        this(true, null, null, zipFile);
    }

    public FMZipProcessResultEvent(String resultDetails, Throwable exception) {
        this(false, resultDetails, exception, null);
    }
}