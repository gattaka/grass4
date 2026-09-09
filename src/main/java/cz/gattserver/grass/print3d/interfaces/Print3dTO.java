package cz.gattserver.grass.print3d.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import cz.gattserver.grass.core.interfaces.ContentNodeBaseTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public class Print3dTO implements ContentNodeBaseTO {

    private final Long id;
    private final String contentReaderId;
    private final Long contentNodeId;
    private final String name;
    private final Long parentId;
    private final String parentName;
    private final LocalDateTime creationDate;
    private final LocalDateTime lastModificationDate;
    private final Long authorId;
    private final String authorName;
    private final boolean hidden;
    private final boolean hiddenByParent;
    private final boolean draft;
    private final Long draftSourceId;
    private final Set<ContentTagTO> contentTags;

    private final String projectDir;

    @QueryProjection
    public Print3dTO(Long id, String contentReaderId,Long contentNodeId, String name, Long parentId, String parentName,
                     LocalDateTime creationDate, LocalDateTime lastModificationDate, Long authorId, String authorName,
                     boolean hidden, boolean hiddenByParent, boolean draft, Long draftSourceId, String projectDir) {
        this(id, contentReaderId, contentNodeId, name, parentId, parentName, creationDate, lastModificationDate, authorId, authorName,
                hidden, hiddenByParent, draft, draftSourceId, new LinkedHashSet<>(), projectDir);
    }
}