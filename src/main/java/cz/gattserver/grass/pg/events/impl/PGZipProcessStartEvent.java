package cz.gattserver.grass.pg.events.impl;

import cz.gattserver.grass.core.events.StartEvent;

import java.io.Serial;
import java.io.Serializable;

public record PGZipProcessStartEvent(int steps) implements StartEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 8280157957343064682L;

}