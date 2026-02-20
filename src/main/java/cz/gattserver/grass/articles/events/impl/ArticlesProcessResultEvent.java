package cz.gattserver.grass.articles.events.impl;

import cz.gattserver.grass.core.events.ResultEvent;

import java.util.UUID;

public record ArticlesProcessResultEvent(UUID operationId, boolean success, String resultDetails) implements ResultEvent {

    public ArticlesProcessResultEvent(UUID operationId) {
        this(operationId,true, null);
    }
}