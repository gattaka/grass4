package cz.gattserver.grass.core.model.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ContentNodeContentTagId implements Serializable {

    @Column(name = "CONTENTNODES_ID")
    private Long contentNodeId;

    @Column(name = "CONTENTTAGS_ID")
    private Long contentTagId;

    public ContentNodeContentTagId(Long contentNodeId, Long contentTagId) {
        this.contentNodeId = contentNodeId;
        this.contentTagId = contentTagId;
    }

    public ContentNodeContentTagId() {
    }

    public Long getContentNodeId() {
        return contentNodeId;
    }

    public void setContentNodeId(Long contentNodeId) {
        this.contentNodeId = contentNodeId;
    }

    public Long getContentTagId() {
        return contentTagId;
    }

    public void setContentTagId(Long contentTagId) {
        this.contentTagId = contentTagId;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof ContentNodeContentTagId that)) return false;

        return contentNodeId.equals(that.contentNodeId) && contentTagId.equals(that.contentTagId);
    }

    @Override
    public int hashCode() {
        int result = contentNodeId.hashCode();
        result = 31 * result + contentTagId.hashCode();
        return result;
    }
}