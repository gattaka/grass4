package cz.gattserver.grass.core.ui.components;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.NodeService;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.grid.contextmenu.GridMenuItem;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.hierarchy.TreeData;
import com.vaadin.flow.data.provider.hierarchy.TreeDataProvider;

import cz.gattserver.grass.core.ui.components.button.CreateGridButton;
import cz.gattserver.grass.core.ui.components.button.GridButton;
import cz.gattserver.grass.core.ui.components.button.ModifyGridButton;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.dialogs.ConfirmDialog;
import cz.gattserver.web.common.ui.dialogs.WebDialog;

public class NodeTree extends VerticalLayout {

	private static final long serialVersionUID = -7457362355620092284L;

	private static final String SMAZAT_LABEL = "Smazat";
	private static final String PREJMENOVAT_LABEL = "Přejmenovat";
	private static final String VYTVORIT_LABEL = "Vytvořit";

	private transient NodeService nodeFacade;

	// Serializable HashMap
	private HashMap<Long, NodeOverviewTO> cache;
	private Set<Long> visited;

	private TreeGrid<NodeOverviewTO> grid;

	// Serializable ArrayList
	private List<NodeOverviewTO> draggedItems;

	public NodeTree() {
		this(false);
	}

	private NodeService getNodeService() {
		if (nodeFacade == null)
			nodeFacade = SpringContextHelper.getBean(NodeService.class);
		return nodeFacade;
	}

	public TreeGrid<NodeOverviewTO> getGrid() {
		return grid;
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

		grid.addHierarchyColumn(NodeOverviewTO::getName).setHeader("Název");
		populate();

		if (enableEditFeatures)
			initEditFeatures();
	}

	private void initEditFeatures() {

		grid.setRowsDraggable(true);
		grid.setDropMode(GridDropMode.ON_TOP_OR_BETWEEN);

		// Register listeners for the dnd events
		grid.addDragStartListener(e -> draggedItems = e.getDraggedItems());

		grid.addDropListener(e -> {
			NodeOverviewTO dropNode = e.getDropTargetItem().get();
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
			for (NodeOverviewTO n : draggedItems)
				moveAction(n, dropNode);
			grid.getDataProvider().refreshAll();
		});

		/*
		 * Context menu
		 */
		GridContextMenu<NodeOverviewTO> gridMenu = grid.addContextMenu();

		GridMenuItem<NodeOverviewTO> smazatMenu = gridMenu.addItem(SMAZAT_LABEL);
		smazatMenu.addMenuItemClickListener(e -> askAndDelete(e.getItem().get()));

		GridMenuItem<NodeOverviewTO> prejmenovatMenu = gridMenu.addItem(PREJMENOVAT_LABEL);
		prejmenovatMenu.addMenuItemClickListener(e -> renameAction(e.getItem().get()));

		GridMenuItem<NodeOverviewTO> vytvoritMenu = gridMenu.addItem(VYTVORIT_LABEL);
		vytvoritMenu.addMenuItemClickListener(e -> createNodeAction(e.getItem()));

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

		CreateGridButton createBtn = new CreateGridButton("Vytvořit",
				e -> createNodeAction(grid.getSelectedItems().isEmpty() ? Optional.empty()
						: Optional.of(grid.getSelectedItems().iterator().next())));
		btnLayout.add(createBtn);

		ModifyGridButton<NodeOverviewTO> modifyBtn = new ModifyGridButton<>(PREJMENOVAT_LABEL, this::renameAction,
				grid);
		btnLayout.add(modifyBtn);

		// mazání chci po jednom
		GridButton<NodeOverviewTO> deleteBtn = new GridButton<>("Smazat",
				nodes -> askAndDelete(nodes.iterator().next()), grid);
		deleteBtn.setIcon(new Image(ImageIcon.DELETE_16_ICON.createResource(), "Smazat"));
		deleteBtn.setEnableResolver(items -> items.size() == 1);
		btnLayout.add(deleteBtn);

	}

	public void populate() {
		List<NodeOverviewTO> nodes = getNodeService().getNodesForTree();
		TreeData<NodeOverviewTO> treeData = new TreeData<>();
		nodes.forEach(n -> cache.put(n.getId(), n));
		nodes.forEach(n -> addTreeItem(treeData, n));
		grid.setDataProvider(new TreeDataProvider<>(treeData));
	}

	private void addTreeItem(TreeData<NodeOverviewTO> treeData, NodeOverviewTO node) {
		if (visited.contains(node.getId()))
			return;
		NodeOverviewTO parent = cache.get(node.getParentId());
		if (parent != null && !visited.contains(parent.getId()))
			addTreeItem(treeData, parent);
		treeData.addItem(parent, node);
		visited.add(node.getId());
	}

	public void expandTo(Long id) {
		NodeOverviewTO to = cache.get(id);
		Long parent = to.getParentId();
		while (parent != null) {
			NodeOverviewTO n = cache.get(parent);
			grid.expand(n);
			parent = n.getParentId();
		}
		grid.select(cache.get(to.getId()));
	}

	private void moveAction(NodeOverviewTO node, NodeOverviewTO newParent) {
		if (node.equals(newParent) || node.getParentId() == null && newParent == null
				|| node.getParentId() != null && newParent != null && node.getParentId().equals(newParent.getId()))
			return; // bez změn

		new ConfirmDialog("Opravdu přesunout '" + node.getName() + "' do "
				+ (newParent == null ? "kořene sekce" : "'" + newParent.getName() + "'") + "?", e -> {
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

	private void askAndDelete(NodeOverviewTO node) {
		if (!getNodeService().isNodeEmpty(node.getId())) {
			UIUtils.showWarning("Kategorie musí být prázdná");
		} else {
			new ConfirmDialog("Opravdu smazat kategorii '" + node.getName() + "' ?", e -> {
				getNodeService().deleteNode(node.getId());
				grid.getTreeData().removeItem(node);
				grid.getDataProvider().refreshAll();
				if (node.getParentId() != null)
					expandTo(node.getParentId());
			}).open();
		}
	}

	private void renameAction(NodeOverviewTO node) {
		final WebDialog dialog = new WebDialog(PREJMENOVAT_LABEL);
		dialog.open();

		final TextField newNameField = new TextField("Nový název:");
		newNameField.setValue(node.getName());
		dialog.add(newNameField);

		HorizontalLayout btnLayout = new HorizontalLayout();
		dialog.addComponent(btnLayout);

		Button confirmBtn = new Button(PREJMENOVAT_LABEL, event -> {
			if (StringUtils.isBlank(newNameField.getValue()))
				UIUtils.showError("Název kategorie nesmí být prázdný");
			try {
				getNodeService().rename(node.getId(), newNameField.getValue());
				node.setName((String) newNameField.getValue());
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

	public void createNodeAction(Optional<NodeOverviewTO> parentNode) {
		final WebDialog dialog = new WebDialog(
				parentNode.isPresent() ? "Vytvořit novou kategorii do '" + parentNode.get().getName() + "'"
						: "Vytvořit novou kořenovou kategorii");
		dialog.open();

		final TextField newNameField = new TextField("Nový název:");
		dialog.addComponent(newNameField);

		HorizontalLayout btnLayout = new HorizontalLayout();
		dialog.addComponent(btnLayout);

		Button confirmBtn = new Button("Vytvořit", event -> {
			if (StringUtils.isBlank(newNameField.getValue()))
				UIUtils.showError("Název kategorie nesmí být prázdný");
			try {
				String newNodeName = newNameField.getValue();
				Long parentNodeId = parentNode.isPresent() ? parentNode.get().getId() : null;
				Long newNodeId = getNodeService().createNewNode(parentNodeId, newNodeName);
				NodeOverviewTO newNode = new NodeOverviewTO();
				newNode.setId(newNodeId);
				newNode.setName(newNodeName);
				newNode.setParentId(parentNodeId);
				cache.put(newNode.getId(), newNode);
				grid.getTreeData().addItem(parentNode.orElse(null), newNode);
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
