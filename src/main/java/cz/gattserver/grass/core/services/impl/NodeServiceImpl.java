package cz.gattserver.grass.core.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cz.gattserver.grass.core.interfaces.NodeTO;
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

    public NodeServiceImpl(NodeRepository nodeRepository, SecurityService securityService) {
        this.nodeRepository = nodeRepository;
        this.securityService = securityService;
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
    public Long createNewNode(Long parentId, boolean hidden, String name) {
        Validate.notBlank(name, "název kategorie nemůže být prázdný");
        Node node = new Node();
        node.setName(name.trim());
        node.setParentId(parentId);
        node.setHidden(hidden);

        if (parentId != null) nodeRepository.findById(parentId)
                .ifPresent(parentNode -> node.setHiddenByParent(node.getHiddenByParent() || node.getHidden()));

        node.setId(nodeRepository.save(node).getId());
        return node.getId();
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
                cycleCheckParent = nodeRepository.findById(cycleCheckParent.getParentId()).orElse(null);
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
    public void hide(Long id) {
        Objects.requireNonNull(id);

        Node node = nodeRepository.findById(id).orElse(null);
        if (node == null) throw new IllegalStateException();

        nodeRepository.updateHidden(id, false);

        // Skrytí podkategorií a obsahů má smysl řešit, pokud tato kategorie byla
        // doposud dle svého předka viditelná, jinak se nic nebude měnit
        if (!node.getHiddenByParent()) return;

        // TODO
    }

    @Override
    public void show(Long id) {
        Objects.requireNonNull(id);
        Node node = nodeRepository.findById(id).orElse(null);
        if (node == null) throw new IllegalStateException();

        nodeRepository.updateHidden(id, false);

        // Zvěřejnění podkategorií a obsahů má smysl řešit, pokud tato kategorie byla
        // doposud dle svého předka viditelná, jinak se nic nebude měnit
        if (!node.getHiddenByParent()) return;

        // TODO
    }
}