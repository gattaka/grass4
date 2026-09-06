package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.interfaces.NodeTO;

import java.util.List;

public interface NodeRepositoryCustom {

    List<NodeTO> findRootNodes(boolean admin);

    int countRootNodes(boolean admin);

    List<NodeTO> findByParentId(Long id, boolean admin);

    List<NodeTO> findByFilter(String filter, boolean admin);

    NodeTO findAndMapById(Long nodeId, boolean admin);

    List<NodeTO> findForTree(boolean admin);

    int countSubNodes(Long nodeId);

    int countContentNodes(Long nodeId);
}