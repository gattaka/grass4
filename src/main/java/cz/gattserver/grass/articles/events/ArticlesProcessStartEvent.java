package cz.gattserver.grass.articles.events;

import cz.gattserver.grass.core.events.StartEvent;

import java.io.Serial;
import java.io.Serializable;

public record ArticlesProcessStartEvent(int steps) implements StartEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 424650899556106622L;
}