package cz.gattserver.grass.pg.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.interfaces.ContentTagTO;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public record PhotogalleryTO(Long id, Long contentId, String name, Long parentId, String parentName,
                             LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId,
                             String authorName, String photogalleryPath, boolean publicated, boolean draft,
                             Long draftSourceId, Set<ContentTagTO> contentTags) implements ContentNodeTO2 {

    @QueryProjection
    public PhotogalleryTO(Long id, Long contentId, String name, Long parentId, String parentName,
                          LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId,
                          String authorName, String photogalleryPath, boolean publicated, boolean draft,
                          Long draftSourceId) {
        this(id, contentId, name, parentId, parentName, creationDate, lastModificationDate, authorId, authorName,
                photogalleryPath, publicated, draft, draftSourceId, new HashSet<>());
    }

    @Override
    public Long contentNodeId() {
        return contentId;
    }
}