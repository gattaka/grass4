package cz.gattserver.grass.core.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.model.repositories.ContentNodeRepository;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import jakarta.validation.constraints.NotNull;
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
    private final ContentNodeRepository contentNodeRepository;

    public NodeServiceImpl(NodeRepository nodeRepository, SecurityService securityService,
                           ContentNodeRepository contentNodeRepository) {
        this.nodeRepository = nodeRepository;
        this.securityService = securityService;
        this.contentNodeRepository = contentNodeRepository;
    }

    @Override
    public NodeTO getNodeById(Long nodeId) {
        return nodeRepository.findAndMapById(nodeId, securityService.getCurrentUser().isAdmin());
    }

    @Override
    public List<NodeTO> getRootNodes() {
        return nodeRepository.findRootNodes(securityService.getCurrentUser().isAdmin());
    }

    @Override
    public int countRootNodes() {
        return nodeRepository.countRootNodes(securityService.getCurrentUser().isAdmin());
    }

    @Override
    public List<NodeTO> getNodesForTree() {
        return nodeRepository.findForTree(securityService.getCurrentUser().isAdmin());
    }

    @Override
    public List<NodeTO> getNodesByParentNode(Long parentId) {
        return nodeRepository.findByParentId(parentId, securityService.getCurrentUser().isAdmin());
    }

    @Override
    public void moveNode(@NotNull Long nodeId, @NotNull Long newParentId) {
        Node nodeEntity = nodeRepository.findById(nodeId).orElseThrow();

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
            cycleCheckParent = cycleCheckParent.getParentId() == null ? null :
                    nodeRepository.findById(cycleCheckParent.getParentId()).orElse(null);
            while (cycleCheckParent != null) {
                if (Objects.equals(cycleCheckParent.getId(), newParentId))
                    throw new IllegalStateException("V grafu kategorií byl nalezen cykl");
                if (Objects.equals(cycleCheckParent.getId(), nodeId))
                    throw new IllegalArgumentException("Nelze vkládat předka do potomka");
                cycleCheckParent = cycleCheckParent.getParentId() == null ? null :
                        nodeRepository.findById(cycleCheckParent.getParentId()).orElse(null);
            }

            nodeEntity.setParentId(newParentId);
        } else {
            nodeEntity.setParentId(null);
        }

        nodeRepository.save(nodeEntity);
    }

    @Override
    public void deleteNode(Long nodeId) {
        int countContents = nodeRepository.countContentNodes(nodeId);
        int countSubNodes = nodeRepository.countSubNodes(nodeId);
        if (countContents + countSubNodes > 0)
            throw new IllegalStateException("Nelze mazat kategorii, ve které existují podkategorie nebo obsahy");
        nodeRepository.deleteById(nodeId);
    }

    @Override
    public void rename(Long nodeId, String newName) {
        Validate.notBlank(newName, "název kategorie nemůže být prázdný");
        nodeRepository.rename(nodeId, newName);
    }

    @Override
    public boolean isNodeEmpty(Long nodeId) {
        int contentNodesCount = nodeRepository.countContentNodes(nodeId);
        int subNodesCount = nodeRepository.countSubNodes(nodeId);
        return contentNodesCount + subNodesCount == 0;
    }

    @Override
    public List<NodeTO> getByFilter(String filter) {
        if (StringUtils.isBlank(filter)) return new ArrayList<>();
        return nodeRepository.findByFilter("%" + filter.toLowerCase() + "%",
                securityService.getCurrentUser().isAdmin());
    }

    @Override
    public Long save(NodeTO to) {
        Objects.requireNonNull(to);
        Node node = new Node();
        node.setId(to.getId());
        node.setName(to.getName());
        node.setParentId(to.getParentId());
        node.setHidden(to.getHidden());
        node.setHiddenByParent(to.getHiddenByParent());

        if (node.getParentId() != null) nodeRepository.findById(node.getParentId()).ifPresent(
                parentNode -> node.setHiddenByParent(parentNode.getHiddenByParent() || parentNode.getHidden()));

        node.setId(nodeRepository.save(node).getId());

        boolean hiddenByParent = node.getHidden() || node.getHiddenByParent();
        contentNodeRepository.updateHiddenByParentByNode(node.getId(), hiddenByParent);
        recursiveNodeSetHiddenByParent(node.getId(), hiddenByParent);

        return node.getId();
    }

    private void recursiveNodeSetHiddenByParent(Long nodeId, boolean hiddenByParent) {
        List<NodeTO> children = nodeRepository.findByParentId(nodeId, true);
        for (NodeTO child : children) {
            nodeRepository.updateHiddenByParent(child.getId(), hiddenByParent);
            recursiveNodeSetHiddenByParent(child.getId(), hiddenByParent);
        }
    }
}