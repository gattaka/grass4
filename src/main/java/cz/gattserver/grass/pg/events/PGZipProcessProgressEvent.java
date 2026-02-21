package cz.gattserver.grass.pg.events;

import cz.gattserver.grass.core.events.ProgressEvent;

import java.io.Serial;
import java.io.Serializable;

public record PGZipProcessProgressEvent(String description) implements ProgressEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 8702688741209054585L;

}