package cz.gattserver.grass.articles.editor.parser.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.interfaces.ContentTagTO;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleDraftOverviewTO(Long id, Long contentNodeId, String name, Long parentId, String parentName,
                                     LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId,
                                     String authorName, boolean publicated, boolean draft,
                                     Set<ContentTagTO> contentTags, Long draftSourceId, String text)
        implements ContentNodeTO2 {

    @QueryProjection
    public ArticleDraftOverviewTO {
    }
}