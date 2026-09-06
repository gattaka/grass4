package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.model.domain.Node;

import java.util.List;

public interface NodeRepositoryCustom {

    // All

    List<NodeTO> findAllRootNodes();

    int countAllRootNodes();

    List<NodeTO> findAllByParentId(Long id);

    int countAllByParentId(Long id);

    List<NodeTO> findAllByFilter(String filter);

    NodeTO findAndMapById(Long nodeId);

    List<NodeTO> findForTree();

    int countSubNodes(Long nodeId);

    int countContentNodes(Long nodeId);

    // Public

    List<NodeTO> findPublicRootNodes();

    int countPublicRootNodes();

    List<NodeTO> findPublicByParentId(Long id);

    int countPublicByParentId(Long id);

    List<NodeTO> findPublicByFilter(String filter);

    NodeTO findPublicAndMapById(Long nodeId);
}