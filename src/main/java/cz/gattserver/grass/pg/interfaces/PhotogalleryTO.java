package cz.gattserver.grass.pg.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeBaseTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public final class PhotogalleryTO implements ContentNodeBaseTO {

    private final Long id;
    private final Long contentNodeId;
    private final String name;
    private final Long parentId;
    private final String parentName;
    private final LocalDateTime creationDate;
    private final LocalDateTime lastModificationDate;
    private final Long authorId;
    private final String authorName;
    private final String photogalleryPath;
    private final boolean hidden;
    private final boolean hiddenByParent;
    private final boolean draft;
    private final Long draftSourceId;
    private final Set<ContentTagTO> contentTags;

    @QueryProjection
    public PhotogalleryTO(Long id, Long contentNodeId, String name, Long parentId, String parentName,
                          LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId,
                          String authorName, String photogalleryPath, boolean hidden, boolean hiddenByParent,
                          boolean draft, Long draftSourceId) {
        this(id, contentNodeId, name, parentId, parentName, creationDate, lastModificationDate, authorId, authorName,
                photogalleryPath, hidden, hiddenByParent, draft, draftSourceId, new HashSet<>());
    }
}