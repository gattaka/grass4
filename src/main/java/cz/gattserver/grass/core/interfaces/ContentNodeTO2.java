package cz.gattserver.grass.core.interfaces;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ContentNodeTO2 {

    Long getParentId();

    String getParentName();

    boolean isPublicated();

    Long getAuthorId();

    String getName();

    String getAuthorName();

    LocalDateTime getCreationDate();

    LocalDateTime getLastModificationDate();

    boolean isDraft();

    Long getContentNodeId();

    Collection<ContentTagTO> getContentTags();
}