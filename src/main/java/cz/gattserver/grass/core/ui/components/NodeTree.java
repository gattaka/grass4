package cz.gattserver.grass.core.ui.components;

import java.io.Serial;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.vaadin.flow.component.icon.VaadinIcon;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.NodeService;
import jakarta.annotation.Nullable;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;

public class NodeTree extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = -1586601854214664708L;

    private static final String SMAZAT_LABEL = "Smazat";
    private static final String PREJMENOVAT_LABEL = "Přejmenovat";
    private static final String VYTVORIT_LABEL = "Vytvořit";

    private transient NodeService nodeService;

    // Serializable HashMap
    private final HashMap<Long, NodeTO> cache;
    private final Set<Long> visited;

    @Getter
    private final TreeGrid<NodeTO> grid;

    // Serializable ArrayList
    private List<NodeTO> draggedItems;

    public NodeTree() {
        this(false);
    }

    private NodeService getNodeService() {
        if (nodeService == null) nodeService = SpringContextHelper.getBean(NodeService.class);
        return nodeService;
    }

    public NodeTree(boolean enableEditFeatures) {
        setSpacing(true);
        setPadding(false);

        cache = new HashMap<>();
        visited = new HashSet<>();

        grid = new TreeGrid<>();
        grid.setSelectionMode(SelectionMode.SINGLE);
        UIUtils.applyGrassDefaultStyle(grid);
        add(grid);
        expand(grid);

        grid.addHierarchyColumn(to -> to.getName() + " " + (!to.getPublicatedByParent() ? VaadinIcon.LOCK : ""))
                .setHeader("Název");
        populate();

        if (enableEditFeatures) initEditFeatures();
    }

    private void initEditFeatures() {
        grid.setRowsDraggable(true);
        grid.setDropMode(GridDropMode.ON_TOP_OR_BETWEEN);

        // Register listeners for the dnd events
        grid.addDragStartListener(e -> draggedItems = e.getDraggedItems());

        grid.addDropListener(e -> {
            Optional<NodeTO> target = e.getDropTargetItem();
            if (target.isEmpty()) return;
            NodeTO dropNode = target.get();
            switch (e.getDropLocation()) {
                case ON_TOP:
                    // vkládám do dropNode
                    break;
                case ABOVE:
                case BELOW:
                    // vkládám do parenta dropNode
                    dropNode = dropNode.getParentId() == null ? null : cache.get(dropNode.getParentId());
                    break;
                case EMPTY:
                default:
                    // výchozí je vkládání do root
                    dropNode = null;
            }
            for (NodeTO n : draggedItems)
                moveAction(n, dropNode);
            grid.getDataProvider().refreshAll();
        });

        /*
         * Context menu
         */
        GridContextMenu<NodeTO> gridMenu = grid.addContextMenu();

        GridMenuItem<NodeTO> smazatMenu = gridMenu.addItem(SMAZAT_LABEL);
        smazatMenu.addMenuItemClickListener(e -> askAndDelete(e.getItem().orElseThrow()));

        GridMenuItem<NodeTO> prejmenovatMenu = gridMenu.addItem(PREJMENOVAT_LABEL);
        prejmenovatMenu.addMenuItemClickListener(e -> renameAction(e.getItem().orElseThrow()));

        GridMenuItem<NodeTO> vytvoritMenu = gridMenu.addItem(VYTVORIT_LABEL);
        vytvoritMenu.addMenuItemClickListener(e -> createNodeAction(e.getItem().orElse(null)));

        gridMenu.addGridContextMenuOpenedListener(e -> {
            smazatMenu.setEnabled(e.getItem().isPresent());
            prejmenovatMenu.setEnabled(e.getItem().isPresent());
        });

        /*
         * Buttons
         */
        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSpacing(true);
        add(btnLayout);

        ComponentFactory componentFactory = new ComponentFactory();
        btnLayout.add(componentFactory.createCreateButton(e -> createNodeAction(
                grid.getSelectedItems().isEmpty() ? null : grid.getSelectedItems().iterator().next())));

        btnLayout.add(componentFactory.createEditGridButton(this::renameAction, grid));

        // mazání chci po jednom
        btnLayout.add(componentFactory.createDeleteGridButton(this::askAndDelete, grid));
    }

    public void populate() {
        List<NodeTO> nodes = getNodeService().getNodesForTree();
        TreeData<NodeTO> treeData = new TreeData<>();
        nodes.forEach(n -> cache.put(n.getId(), n));
        nodes.forEach(n -> addTreeItem(treeData, n));
        grid.setDataProvider(new TreeDataProvider<>(treeData));
    }

    private void addTreeItem(TreeData<NodeTO> treeData, NodeTO node) {
        if (visited.contains(node.getId())) return;
        NodeTO parent = cache.get(node.getParentId());
        if (parent != null && !visited.contains(parent.getId())) addTreeItem(treeData, parent);
        treeData.addItem(parent, node);
        visited.add(node.getId());
    }

    public void expandTo(Long id) {
        NodeTO to = cache.get(id);
        Long parent = to.getParentId();
        while (parent != null) {
            NodeTO n = cache.get(parent);
            grid.expand(n);
            parent = n.getParentId();
        }
        grid.select(cache.get(to.getId()));
    }

    private void moveAction(NodeTO node, NodeTO newParent) {
        if (node.equals(newParent) || node.getParentId() == null && newParent == null ||
                node.getParentId() != null && newParent != null && node.getParentId().equals(newParent.getId()))
            return; // bez změn

        new ConfirmDialog("Opravdu přesunout '" + node.getName() + "' do " +
                (newParent == null ? "kořene sekce" : "'" + newParent.getName() + "'") + "?", e -> {
            try {
                getNodeService().moveNode(node.getId(), newParent == null ? null : newParent.getId());
                node.setParentId(newParent == null ? null : newParent.getId());
                grid.getTreeData().setParent(node, newParent);
                grid.getDataProvider().refreshAll();
                expandTo(node.getId());
            } catch (IllegalArgumentException ex) {
                UIUtils.showWarning("Nelze přesunou předka do potomka");
            }
        }).open();
    }

    private void askAndDelete(NodeTO node) {
        if (!getNodeService().isNodeEmpty(node.getId())) {
            UIUtils.showWarning("Kategorie musí být prázdná");
        } else {
            getNodeService().deleteNode(node.getId());
            grid.getTreeData().removeItem(node);
            grid.getDataProvider().refreshAll();
            if (node.getParentId() != null) expandTo(node.getParentId());
        }
    }

    private void renameAction(NodeTO node) {
        final WebDialog dialog = new WebDialog(PREJMENOVAT_LABEL);
        dialog.open();

        final TextField newNameField = new TextField("Nový název:");
        newNameField.setValue(node.getName());
        dialog.add(newNameField);

        HorizontalLayout btnLayout = new HorizontalLayout();
        dialog.addComponent(btnLayout);

        Button confirmBtn = new Button(PREJMENOVAT_LABEL, event -> {
            if (StringUtils.isBlank(newNameField.getValue())) UIUtils.showError("Název kategorie nesmí být prázdný");
            try {
                getNodeService().rename(node.getId(), newNameField.getValue());
                node.setName(newNameField.getValue());
                grid.getDataProvider().refreshItem(node);
                expandTo(node.getId());
            } catch (Exception e) {
                UIUtils.showWarning("Přejmenování se nezdařilo.");
            }

            dialog.close();
        });
        btnLayout.add(confirmBtn);

        Button closeBtn = new Button("Storno", event -> dialog.close());
        btnLayout.add(closeBtn);
    }

    private void createNodeAction(@Nullable NodeTO parentNode) {
        final WebDialog dialog = new WebDialog(
                parentNode != null ? "Vytvořit novou kategorii do '" + parentNode.getName() + "'" :
                        "Vytvořit novou kořenovou kategorii");
        dialog.open();

        // TODO publicated
        boolean publicated = true;

        final TextField newNameField = new TextField("Nový název:");
        dialog.addComponent(newNameField);

        HorizontalLayout btnLayout = new HorizontalLayout();
        dialog.addComponent(btnLayout);

        Button confirmBtn = new Button("Vytvořit", event -> {
            if (StringUtils.isBlank(newNameField.getValue())) UIUtils.showError("Název kategorie nesmí být prázdný");
            try {
                String newNodeName = newNameField.getValue();
                Long parentNodeId = parentNode == null ? null : parentNode.getId();
                Long newNodeId = getNodeService().createNewNode(parentNodeId, publicated, newNodeName);
                NodeTO newNode = new NodeTO();
                newNode.setId(newNodeId);
                newNode.setName(newNodeName);
                newNode.setParentId(parentNodeId);
                cache.put(newNode.getId(), newNode);
                grid.getTreeData().addItem(parentNode, newNode);
                grid.getDataProvider().refreshAll();
                expandTo(newNodeId);
            } catch (Exception ex) {
                UIUtils.showWarning("Vytvoření se nezdařilo.");
            }

            dialog.close();
        });
        btnLayout.add(confirmBtn);

        Button closeBtn = new Button("Storno", event -> dialog.close());
        btnLayout.add(closeBtn);
    }

}
