package cz.gattserver.grass.pg.events.impl;

import cz.gattserver.grass.core.events.ResultEvent;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record PGProcessResultEvent(boolean success, String resultDetails, Long galleryId, UUID operationId)
        implements ResultEvent, Serializable {

    @Serial
    private static final long serialVersionUID = -6357680911232905029L;

    public PGProcessResultEvent(UUID operationId, Long galleryId) {
        this(true, null, galleryId, operationId);
    }

    public PGProcessResultEvent(UUID operationId, boolean success, String resultDetails) {
        this(success, resultDetails, null, operationId);
    }
}