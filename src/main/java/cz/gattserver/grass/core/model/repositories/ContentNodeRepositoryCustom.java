package cz.gattserver.grass.core.model.repositories;

import com.querydsl.core.QueryResults;

import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentNodeOverviewTO;

public interface ContentNodeRepositoryCustom {

	QueryResults<ContentNodeOverviewTO> findByTagAndUserAccess(Long tagId, Long userId, boolean admin, int offset,
                                                               int limit);

	QueryResults<ContentNodeOverviewTO> findByUserFavouritesAndUserAccess(Long favouritesUserId, Long userId,
			boolean admin, int offset, int limit);

	QueryResults<ContentNodeOverviewTO> findByFilterAndUserAccess(ContentNodeFilterTO filter, Long userId,
                                                                  boolean admin, int offset, int limit, String sortProperty);

}
