package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.model.domain.ContentTag;

import java.util.Set;

public interface ContentNodeContentTagRepositoryCustom {

    Set<ContentTagTO> findByContendNodeIdAndMap(Long contentNodeId);

    Set<ContentTag> findByContendNodeId(Long contentNodeId);
}