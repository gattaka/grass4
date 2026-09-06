package cz.gattserver.grass.core.ui.components;

import java.io.Serial;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
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

    private final NodeService nodeService;

    // Serializable HashMap
    private final HashMap<Long, NodeTO> cache;
    private final Set<Long> visited;

    @Getter
    private final TreeGrid<NodeTO> grid;

    // Serializable ArrayList
    private List<NodeTO> draggedItems;

    public NodeTree() {
        nodeService = SpringContextHelper.getBean(NodeService.class);

        setSpacing(true);
        setPadding(false);

        cache = new HashMap<>();
        visited = new HashSet<>();

        grid = new TreeGrid<>();
        grid.setSelectionMode(SelectionMode.SINGLE);
        UIUtils.applyGrassDefaultStyle(grid);
        add(grid);
        expand(grid);

        grid.addComponentHierarchyColumn(to -> {
            Div div = new Div(new Text(to.getName()));
            if (to.getHiddenByParent() && to.getHidden()) div.add(VaadinIcon.LOCK.create());
            return div;
        }).setHeader("Název");
        populate();
    }

    public void populate() {
        List<NodeTO> nodes = nodeService.getNodesForTree();
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
}