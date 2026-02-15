package cz.gattserver.grass.core.model.repositories;

import java.util.List;
import java.util.Optional;

public interface ContentTagRepositoryCustom {

	int countContentTagContents(Long id);

	List<String> findByFilter(Optional<String> filter, int offset, int limit);

	Integer countByFilter(Optional<String> filter);

}