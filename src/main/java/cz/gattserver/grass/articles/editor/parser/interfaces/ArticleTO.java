package cz.gattserver.grass.articles.editor.parser.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.interfaces.ContentTagTO;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public record ArticleTO(Long id, Long contentNodeId, String name, Long parentId, String parentName,
                        LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId,
                        String authorName, boolean publicated, boolean draft, Long draftSourceId,
                        Set<ContentTagTO> contentTags, String outputHTML, String text, String searchableOutput,
                        String attachmentsDirId, Set<String> pluginCSSResources, Set<String> pluginJSResources,
                        Set<String> pluginJSCodes) implements ContentNodeTO2 {

    @QueryProjection
    public ArticleTO(Long id, Long contentNodeId, String name, Long parentId, String parentName,
                     LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId, String authorName,
                     boolean publicated, boolean draft, Long draftSourceId, String outputHTML, String text,
                     String searchableOutput, String attachmentsDirId) {
        this(id, contentNodeId, name, parentId, parentName, creationDate, lastModificationDate, authorId, authorName,
                publicated, draft, draftSourceId, new LinkedHashSet<>(), outputHTML, text, searchableOutput, attachmentsDirId,
                new LinkedHashSet<>(), new LinkedHashSet<>(), new LinkedHashSet<>());
    }
}