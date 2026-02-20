package cz.gattserver.grass.fm.events;

import cz.gattserver.grass.core.events.ProgressEvent;

import java.io.Serial;
import java.io.Serializable;

public record FMZipProcessProgressEvent(String description) implements ProgressEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 7316903584148999475L;

}