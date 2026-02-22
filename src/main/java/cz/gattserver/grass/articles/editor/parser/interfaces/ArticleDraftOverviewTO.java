package cz.gattserver.grass.articles.editor.parser.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.interfaces.ContentTagTO;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public record ArticleDraftOverviewTO(Long id, String name, LocalDateTime creationDate,
                                     LocalDateTime lastModificationDate, String text) {

    @QueryProjection
    public ArticleDraftOverviewTO {
    }
}