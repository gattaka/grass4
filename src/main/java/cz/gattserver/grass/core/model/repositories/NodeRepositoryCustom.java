package cz.gattserver.grass.core.model.repositories;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.model.domain.Node;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NodeRepositoryCustom {

    List<NodeTO> findAllRootNodes();

    int countAllRootNodes();

    List<NodeTO> findAllByParentId(Long id);

    int countAllByParentId(Long id);

    List<Node> findPublicRootNodes();

    int countPublicRootNodes();

    List<Node> findPublicByParentId(Long id);

    int countPublicByParentId(Long id);

    List<NodeTO> findAllByFilter(String filter);

    List<NodeTO> findPublicByFilter(String filter);

    NodeTO findAndMapById(Long nodeId);

    List<NodeTO> findForTree();

    int countSubNodes(Long nodeId);

    int countContentNodes(Long nodeId);
}