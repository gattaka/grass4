package cz.gattserver.grass.articles.editor.parser.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeBaseTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@AllArgsConstructor
public final class ArticleTO implements ContentNodeBaseTO {

    private final Long id;
    private final Long contentNodeId;
    private final String name;
    private final Long parentId;
    private final String parentName;
    private final LocalDateTime creationDate;
    private final LocalDateTime lastModificationDate;
    private final Long authorId;
    private final String authorName;
    private final boolean publicated;
    private final boolean publicatedByParent;
    private final boolean draft;
    private final Long draftSourceId;
    private final Set<ContentTagTO> contentTags;
    private final String outputHTML;
    private final String text;
    private final String searchableOutput;
    private final String attachmentsDirId;
    private final Set<String> pluginCSSResources;
    private final Set<String> pluginJSResources;
    private final Set<String> pluginJSCodes;

    @QueryProjection
    public ArticleTO(Long id, Long contentNodeId, String name, Long parentId, String parentName,
                     LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId, String authorName,
                     boolean publicated, boolean publicatedByParent, boolean draft, Long draftSourceId,
                     String outputHTML, String text, String searchableOutput, String attachmentsDirId) {
        this(id, contentNodeId, name, parentId, parentName, creationDate, lastModificationDate, authorId, authorName,
                publicated, publicatedByParent, draft, draftSourceId, new LinkedHashSet<>(), outputHTML, text,
                searchableOutput, attachmentsDirId, new LinkedHashSet<>(), new LinkedHashSet<>(),
                new LinkedHashSet<>());
    }
}