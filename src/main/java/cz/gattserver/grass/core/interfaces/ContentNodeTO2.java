package cz.gattserver.grass.core.interfaces;

import java.time.LocalDateTime;
import java.util.Collection;

public interface ContentNodeTO2 {

    Long parentId();

    String parentName();

    boolean publicated();

    Long authorId();

    String name();

    String authorName();

    LocalDateTime creationDate();

    LocalDateTime lastModificationDate();

    boolean draft();

    Long getContentNodeId();

    Collection<ContentTagTO> contentTags();
}