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

    private String createExplicitAccessValue(Long nodeId) {
        return "NODE" + nodeId;
    }

    @Override
    public String createExplicitAccessHash(Long nodeId) {
        return securityService.computeAccessHash(createExplicitAccessValue(nodeId));
    }

    @Override
    public NodeTO getNodeById(Long nodeId) {
        return innerGetNodeById(nodeId, null);
    }

    @Override
    public NodeTO getNodeById(Long nodeId, String explicitAccessHash) {
        return innerGetNodeById(nodeId, explicitAccessHash);
    }

    private NodeTO innerGetNodeById(Long nodeId, String explicitAccessHash) {
        boolean access = createExplicitAccessHash(nodeId).equals(explicitAccessHash) ||
                securityService.getCurrentUser().isAdmin();
        return nodeRepository.findAndMapById(nodeId, access);
    }

    @Override
    public List<NodeTO> getRootNodes() {
        return nodeRepository.findRootNodes(securityService.getCurrentUser().isAdmin());
    }

    @Override
    public List<NodeTO> getNodesForTree() {
        return nodeRepository.findForTree(securityService.getCurrentUser().isAdmin());
    }

    @Override
    public List<NodeTO> getNodesByParentNode(Long parentId, boolean explicitAccess) {
        return nodeRepository.findByParentId(parentId, explicitAccess || securityService.getCurrentUser().isAdmin());
    }

    @Override
    public void moveNode(@NotNull Long nodeId, @NotNull Long newParentId) {
        Node node = nodeRepository.findById(nodeId).orElseThrow();

        // beze změn
        if (Objects.equals(node.getParentId(), newParentId)) return;

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

            node.setParentId(newParentId);
        } else {
            node.setParentId(null);
        }

        if (node.getParentId() != null) nodeRepository.findById(node.getParentId()).ifPresent(
                parentNode -> node.setHiddenByParent(parentNode.getHiddenByParent() || parentNode.getHidden()));

        updateHiddenByParent(node.getId(), node.getHidden() || node.getHiddenByParent());

        nodeRepository.save(node);
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
        Validate.notBlank(to.getName());

        Node node = new Node();
        node.setId(to.getId());
        node.setName(to.getName());
        node.setParentId(to.getParentId());
        node.setHidden(to.getHidden());
        node.setHiddenByParent(false);

        if (node.getParentId() != null) nodeRepository.findById(node.getParentId()).ifPresent(
                parentNode -> node.setHiddenByParent(parentNode.getHiddenByParent() || parentNode.getHidden()));

        node.setId(nodeRepository.save(node).getId());

        updateHiddenByParent(node.getId(), node.getHidden() || node.getHiddenByParent());

        return node.getId();
    }

    private void updateHiddenByParent(Long nodeId, boolean hiddenByParent) {
        contentNodeRepository.updateHiddenByParent(nodeId, hiddenByParent);
        nodeRepository.updateHiddenByParent(nodeId, hiddenByParent);
        List<NodeTO> children = nodeRepository.findByParentId(nodeId, true);
        for (NodeTO child : children)
            updateHiddenByParent(child.getId(), hiddenByParent);
    }
}