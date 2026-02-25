package cz.gattserver.grass.articles.events;

import cz.gattserver.grass.core.events.ProgressEvent;

import java.io.Serial;
import java.io.Serializable;

public record ArticlesProcessProgressEvent(String description) implements ProgressEvent, Serializable {

    @Serial
    private static final long serialVersionUID = -4979709394202605079L;
}