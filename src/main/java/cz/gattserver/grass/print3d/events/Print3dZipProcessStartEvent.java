package cz.gattserver.grass.print3d.events;

import cz.gattserver.grass.core.events.StartEvent;

import java.io.Serial;
import java.io.Serializable;

public record Print3dZipProcessStartEvent(int steps) implements StartEvent, Serializable {

    @Serial
    private static final long serialVersionUID = -8854826898744000205L;

}