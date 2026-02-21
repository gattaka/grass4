package cz.gattserver.grass.pg.events;

import cz.gattserver.grass.core.events.ProgressEvent;

import java.io.Serial;
import java.io.Serializable;

public record PGProcessProgressEvent(String description) implements ProgressEvent, Serializable {

    @Serial
    private static final long serialVersionUID = -6476285236133060485L;

}