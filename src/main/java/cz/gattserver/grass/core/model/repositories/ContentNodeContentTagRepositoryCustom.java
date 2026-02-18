package cz.gattserver.grass.core.model.repositories;

import com.querydsl.core.QueryResults;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;
import cz.gattserver.grass.core.interfaces.ContentTagTO;

import java.util.List;
import java.util.Set;

public interface ContentNodeContentTagRepositoryCustom {

    Set<ContentTagTO> findByContendNodeId(Long contentNodeId);
}