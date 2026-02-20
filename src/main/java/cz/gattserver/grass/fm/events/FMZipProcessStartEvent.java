package cz.gattserver.grass.fm.events;

import cz.gattserver.grass.core.events.StartEvent;

import java.io.Serial;
import java.io.Serializable;

public record FMZipProcessStartEvent(int steps) implements StartEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 2123372385202553332L;

}