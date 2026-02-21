package cz.gattserver.grass.pg.events;

import cz.gattserver.grass.core.events.ResultEvent;

import java.io.Serial;
import java.io.Serializable;
import java.nio.file.Path;

public record PGZipProcessResultEvent(boolean success, String resultDetails, Throwable resultException, Path zipFile)
        implements ResultEvent, Serializable {

    @Serial
    private static final long serialVersionUID = -5648513526550150215L;

    public PGZipProcessResultEvent(Path zipFile) {
        this(true, null, null, zipFile);
    }

    public PGZipProcessResultEvent(String resultDetails, Throwable exception) {
        this(false, resultDetails, exception, null);
    }

}