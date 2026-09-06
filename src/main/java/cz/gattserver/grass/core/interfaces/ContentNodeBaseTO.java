package cz.gattserver.grass.core.interfaces;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ContentNodeBaseTO {

    String getName();

    Long getParentId();

    String getParentName();

    boolean isHidden();

    boolean isHiddenByParent();

    Long getAuthorId();

    String getAuthorName();

    LocalDateTime getCreationDate();

    LocalDateTime getLastModificationDate();

    boolean isDraft();

    Long getDraftSourceId();

    Long getContentNodeId();

    Collection<ContentTagTO> getContentTags();
}