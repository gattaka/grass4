package cz.gattserver.grass.pg.events.impl;

import cz.gattserver.grass.core.events.StartEvent;

import java.io.Serial;
import java.io.Serializable;

public record PGProcessStartEvent(int steps) implements StartEvent, Serializable {

    @Serial
    private static final long serialVersionUID = -5220589703922871979L;

}