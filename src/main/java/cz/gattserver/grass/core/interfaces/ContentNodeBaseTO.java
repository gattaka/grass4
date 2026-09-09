package cz.gattserver.grass.core.interfaces;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ContentNodeBaseTO {

    Long getId();

    Long getContentNodeId();

    String getContentReaderId();

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

    Collection<ContentTagTO> getContentTags();
}