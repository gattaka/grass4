package cz.gattserver.grass.core.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cz.gattserver.grass.core.model.domain.Node;
import cz.gattserver.grass.core.model.repositories.NodeRepository;

@Transactional
@Service
public class NodeServiceImpl implements NodeService {

    private final NodeRepository nodeRepository;
    private final SecurityService securityService;

    public NodeServiceImpl(NodeRepository nodeRepository, SecurityService securityService) {
        this.nodeRepository = nodeRepository;
        this.securityService = securityService;
    }

    @Override
    public NodeTO getNodeById(long nodeId) {
        return nodeRepository.findAndMapById(nodeId);
    }

    @Override
    public List<NodeTO> getRootNodes() {
        return nodeRepository.findAllRootNodes();
    }

    @Override
    public int countRootNodes() {
        return nodeRepository.countAllRootNodes();
    }

    @Override
    public List<NodeTO> getNodesForTree() {
        return nodeRepository.findForTree();
    }

    @Override
    public List<NodeTO> getNodesByParentNode(long parentId) {
        return nodeRepository.findAllByParentId(parentId);
    }

    @Override
    public int countNodesByParentNode(long parentId) {
        return nodeRepository.countAllByParentId(parentId);
    }

    @Override
    public long createNewNode(Long parentId, String name) {
        Validate.notBlank(name, "název kategorie nemůže být prázdný");
        Node node = new Node();
        node.setName(name.trim());
        node.setParentId(parentId);
        node = nodeRepository.save(node);
        return node.getId();
    }

    @Override
    public void moveNode(long nodeId, Long newParentId) {
        Node nodeEntity = nodeRepository.findById(nodeId).orElse(null);

        // beze změn
        if (Objects.equals(nodeEntity.getParentId(), newParentId)) return;

        Node newParentEntity = newParentId == null ? null : nodeRepository.findById(newParentId).orElse(null);

        // zamezí vkládání předků do potomků - projde postupně všechny předky
        // cílové kategorie a pokud narazí na moje id, pak jsem předkem cílové
        // kategorie, což je špatně
        if (newParentEntity != null) {
            Node cycleCheckParent = newParentEntity;
            // začínám od předka newParent - tohle je schválně, umožní mi to se
            // pak ptát na id newParent - pokud totiž narazím na newParent id,
            // pak je v DB cykl
            cycleCheckParent = nodeRepository.findById(cycleCheckParent.getParentId()).orElse(null);
            while (cycleCheckParent != null) {
                if (cycleCheckParent.getId() == newParentId)
                    throw new IllegalStateException("V grafu kategorií byl nalezen cykl");
                if (cycleCheckParent.getId() == nodeId)
                    throw new IllegalArgumentException("Nelze vkládat předka do potomka");
                cycleCheckParent = nodeRepository.findById(cycleCheckParent.getParentId()).orElse(null);
            }

            nodeEntity.setParentId(newParentId);
        } else {
            nodeEntity.setParentId(null);
        }

        nodeRepository.save(nodeEntity);
    }

    @Override
    public void deleteNode(long nodeId) {
        int countContents = nodeRepository.countContentNodes(nodeId);
        int countSubNodes = nodeRepository.countSubNodes(nodeId);
        if (countContents + countSubNodes > 0)
            throw new IllegalStateException("Nelze mazat kategorii, ve které existují podkategorie nebo obsahy");
        nodeRepository.deleteById(nodeId);
    }

    @Override
    public void rename(long nodeId, String newName) {
        Validate.notBlank(newName, "název kategorie nemůže být prázdný");
        nodeRepository.rename(nodeId, newName);
    }

    @Override
    public boolean isNodeEmpty(long nodeId) {
        int contentNodesCount = nodeRepository.countContentNodes(nodeId);
        int subNodesCount = nodeRepository.countSubNodes(nodeId);
        return contentNodesCount + subNodesCount == 0;
    }

    @Override
    public List<NodeTO> getByFilter(String filter) {
        if (StringUtils.isBlank(filter)) return new ArrayList<>();

        if (securityService.getCurrentUser().isAdmin()) {
            return nodeRepository.findAllByFilter("%" + filter.toLowerCase() + "%");
        } else {
            return nodeRepository.findPublicByFilter("%" + filter.toLowerCase() + "%");
        }
    }

}
