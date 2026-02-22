package cz.gattserver.grass.articles.editor.parser.interfaces;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record ArticleDraftOverviewTO(Long id, String name, LocalDateTime creationDate,
                                     LocalDateTime lastModificationDate, String text) {

    @QueryProjection
    public ArticleDraftOverviewTO {
    }
}