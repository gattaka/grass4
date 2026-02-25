package cz.gattserver.grass.articles.events;

import cz.gattserver.grass.core.events.ResultEvent;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record ArticlesProcessResultEvent(UUID operationId, boolean success, String resultDetails)
        implements ResultEvent, Serializable {

    @Serial
    private static final long serialVersionUID = 6889189065708741530L;

    public ArticlesProcessResultEvent(UUID operationId) {
        this(operationId, true, null);
    }
}