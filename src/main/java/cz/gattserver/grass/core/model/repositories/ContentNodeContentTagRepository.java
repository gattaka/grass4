package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.model.domain.ContentNodeContentTag;
import cz.gattserver.grass.core.model.domain.ContentNodeContentTagId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentNodeContentTagRepository
        extends JpaRepository<ContentNodeContentTag, ContentNodeContentTagId>, ContentNodeContentTagRepositoryCustom {
}