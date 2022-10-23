package cz.gattserver.grass.core.model.repositories;

import java.util.List;

public interface ContentTagRepositoryCustom {

	int countContentTagContents(Long id);

	List<String> findByFilter(String filter, int offset, int limit);

	Integer countByFilter(String filter);

}
