package cz.gattserver.grass.core.model.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity(name = "CONTENT_NODE_CONTENT_TAG")
public class ContentNodeContentTag {

    @EmbeddedId
    private ContentNodeContentTagId id;

    public ContentNodeContentTag() {
    }

    public ContentNodeContentTag(Long contentNodeId, Long contentTagId) {
        this.id = new ContentNodeContentTagId(contentNodeId, contentTagId);
    }

    public ContentNodeContentTag(ContentNodeContentTagId id) {
        this.id = id;
    }

    public ContentNodeContentTagId getId() {
        return id;
    }

    public void setId(ContentNodeContentTagId id) {
        this.id = id;
    }
}