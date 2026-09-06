package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.interfaces.ContentTagTO;
import jakarta.annotation.Nullable;

import java.util.List;

public interface ContentTagRepositoryCustom {

    int countContentTagContents(Long id, boolean admin);

    List<String> findByFilter(@Nullable String filter, boolean admin, int offset, int limit);

    Integer countByFilter(@Nullable String filter, boolean admin);

    List<Integer> findContentNodesCountsGroups(boolean admin);

    List<ContentTagTO> findAllOrderByContentCountNode(boolean admin);

    List<ContentTagTO> findAllOrderByNameCaseInsensitive(boolean admin);

    ContentTagTO findAndMapById(Long id, boolean admin);

    ContentTagTO findAndMapByName(String name, boolean admin);
}