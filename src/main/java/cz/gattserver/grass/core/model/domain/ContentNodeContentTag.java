package cz.gattserver.grass.core.model.domain;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

@Entity(name = "CONTENTNODE_CONTENT_TAG")
public class ContentNodeContentTag {

    @EmbeddedId
    private ContentNodeContentTagId id;

    public ContentNodeContentTag() {
    }

    public ContentNodeContentTag(Long medicalRecordId, Long medicamentId) {
        this.id = new ContentNodeContentTagId(medicalRecordId, medicamentId);
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