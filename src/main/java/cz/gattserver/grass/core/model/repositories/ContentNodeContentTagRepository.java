package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.model.domain.ContentNodeContentTag;
import cz.gattserver.grass.core.model.domain.ContentNodeContentTagId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ContentNodeContentTagRepository
        extends JpaRepository<ContentNodeContentTag, ContentNodeContentTagId>, ContentNodeContentTagRepositoryCustom {

    @Modifying
    @Query("delete CONTENT_NODE_CONTENT_TAG where id.contentNodeId = ?1")
    void deleteByContentId(long contentNodeId);
}