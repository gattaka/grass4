package cz.gattserver.grass.core.ui.pages;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.dialogs.MoveIntoNodeDialog;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import cz.gattserver.grass.core.ui.components.Breadcrumb;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import cz.gattserver.grass.core.ui.components.NewContentNodeGrid;
import cz.gattserver.grass.core.ui.components.NodesGrid;
import cz.gattserver.grass.core.ui.util.UIUtils;

@Route(value = "category", layout = MainView.class)
public class NodePage extends Div implements HasUrlParameter<String>, HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = -3061117526398690714L;

    private final ContentNodeService contentNodeService;
    private final NodeService nodeService;
    private final CoreACLService coreACLService;
    private final SecurityService securityService;

    private NodeTO nodeTO;

    public NodePage(ContentNodeService contentNodeService, NodeService nodeService, CoreACLService coreACLService,
                    SecurityService securityService) {
        this.contentNodeService = contentNodeService;
        this.nodeService = nodeService;
        this.coreACLService = coreACLService;
        this.securityService = securityService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
        if (identifier == null) throw new GrassPageException(404);

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        nodeTO = nodeService.getNodeById(identifier.id());

        // Navigační breadcrumb
        createBreadcrumb(layout, nodeTO);

        // Podkategorie
        createSubnodesPart(layout, nodeTO);

        // Obsahy
        createContentsPart(layout, nodeTO);
    }

    public void createNodeAction(NodeTO parentNode) {
        final WebDialog dialog = new WebDialog("Vytvořit kategorii");
        dialog.setWidth(350, Unit.PIXELS);

        final TextField newNameField = new TextField();
        newNameField.setPlaceholder("Nová kategorie do " + parentNode.getName());
        newNameField.setWidthFull();
        dialog.addComponent(newNameField);

        final Checkbox hiddenCheckbox = new Checkbox("Skrytá kategorie");
        dialog.addComponent(hiddenCheckbox);

        NodeTO to = new NodeTO();
        Binder<NodeTO> binder = new Binder<>(NodeTO.class);
        binder.forField(newNameField).withValidator(StringUtils::isNotBlank, "Název kategorie nesmí být prázdný")
                .bind(NodeTO::getName, NodeTO::setName);
        binder.forField(hiddenCheckbox).bind(NodeTO::getHidden, NodeTO::setHidden);
        binder.setBean(to);

        ComponentFactory componentFactory = new ComponentFactory();
        dialog.getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(event -> {
            if (binder.validate().isOk()) {
                Long newNodeId = nodeService.createNewNode(parentNode.getId(), to.getHidden(), to.getName());
                UI.getCurrent()
                        .navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(newNodeId, to.getName()));
                dialog.close();
            }
        }, event -> dialog.close()));

        dialog.open();
    }

    private void createBreadcrumb(Div layout, NodeTO node) {
        Breadcrumb breadcrumb = new Breadcrumb();
        layout.add(breadcrumb);

        // pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
        // aktuální polohu cílové kategorie
        List<Breadcrumb.BreadcrumbElement> breadcrumbElements = new ArrayList<>();
        NodeTO parent = node;
        while (true) {

            // nejprve zkus zjistit, zda předek existuje
            if (parent == null) throw new GrassPageException(404);

            breadcrumbElements.add(new Breadcrumb.BreadcrumbElement(parent.getName(), NodePage.class,
                    URLIdentifierUtils.createURLIdentifier(parent.getId(), parent.getName())));

            // pokud je můj předek null, pak je to konec a je to všechno
            if (parent.getParentId() == null) break;

            parent = nodeService.getNodeById(parent.getParentId());
        }

        breadcrumb.resetBreadcrumb(breadcrumbElements);
    }

    private void populateSubNodes(NodesGrid nodesGrid, Long parentNodeId) {
        List<NodeTO> nodes = nodeService.getNodesByParentNode(parentNodeId);
        if (nodes == null) throw new GrassPageException(500);
        nodesGrid.populate(nodes);
    }

    private void createSubnodesPart(Div layout, NodeTO node) {
        layout.add(new H2("Podkategorie"));

        boolean admin = securityService.getCurrentUser().isAdmin();
        NodesGrid nodesGrid = new NodesGrid(admin);
        populateSubNodes(nodesGrid, node.getId());

        layout.add(nodesGrid);
        nodesGrid.setWidthFull();

        if (admin) {
            nodesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

            ComponentFactory componentFactory = new ComponentFactory();

            Div buttonLayout = componentFactory.createButtonLayout();
            buttonLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            layout.add(buttonLayout);

            Button createButton = componentFactory.createCreateButton(e -> createNodeAction(nodeTO));
            buttonLayout.add(createButton);

            Button renameButton =
                    componentFactory.createEditGridButton(toRenameTO -> onRenameNode(nodesGrid, toRenameTO), nodesGrid);
            buttonLayout.add(renameButton);

            Button moveBtn = componentFactory.createMoveGridButton(set -> {
                NodeTO toMoveTO = set.iterator().next();
                new MoveIntoNodeDialog(toMoveTO.getParentId(),
                        newParentTO -> onMoveAction(nodesGrid, toMoveTO, newParentTO)).open();
            }, nodesGrid);
            buttonLayout.add(moveBtn);

            Button hideButton =
                    componentFactory.createHideGridButton(toHideTO -> onHideNode(nodesGrid, toHideTO.getId()),
                            nodesGrid);
            buttonLayout.add(hideButton);

            Button showButton =
                    componentFactory.createShowGridButton(toShowTO -> onShowNode(nodesGrid, toShowTO.getId()),
                            nodesGrid);
            buttonLayout.add(showButton);

            nodesGrid.addSelectionListener(e -> e.getFirstSelectedItem().ifPresent(selectedTO -> {
                boolean hidden = selectedTO.getHidden() || selectedTO.getHiddenByParent();
                hideButton.setEnabled(!hidden);
                showButton.setEnabled(hidden);
            }));

            buttonLayout.add(
                    componentFactory.createDeleteGridButton(toDeleteTO -> onDeleteNode(nodesGrid, toDeleteTO.getId()),
                            nodesGrid));
        }
    }

    private void onMoveAction(NodesGrid nodesGrid, NodeTO toMoveTO, NodeTO newParentTO) {
        if (toMoveTO.equals(newParentTO) || toMoveTO.getParentId() == null && newParentTO == null ||
                toMoveTO.getParentId() != null && newParentTO != null &&
                        toMoveTO.getParentId().equals(newParentTO.getId())) return; // bez změn

        new ConfirmDialog("Opravdu přesunout '" + toMoveTO.getName() + "' do " +
                (newParentTO == null ? "kořene sekce" : "'" + newParentTO.getName() + "'") + "?", e -> {
            try {
                nodeService.moveNode(toMoveTO.getId(), newParentTO == null ? null : newParentTO.getId());
                toMoveTO.setParentId(newParentTO == null ? null : newParentTO.getId());
                populateSubNodes(nodesGrid, toMoveTO.getId());
            } catch (IllegalArgumentException ex) {
                UIUtils.showWarning("Nelze přesunou předka do potomka");
            }
        }).open();
    }

    private void onHideNode(NodesGrid nodesGrid, Long nodeId) {
        nodeService.hide(nodeId);
        populateSubNodes(nodesGrid, nodeId);
    }

    private void onShowNode(NodesGrid nodesGrid, Long nodeId) {
        nodeService.show(nodeId);
        populateSubNodes(nodesGrid, nodeId);
    }

    private void onDeleteNode(NodesGrid nodesGrid, Long nodeId) {
        if (!nodeService.isNodeEmpty(nodeId)) {
            UIUtils.showWarning("Kategorie musí být prázdná");
        } else {
            nodeService.deleteNode(nodeId);
            populateSubNodes(nodesGrid, nodeId);
        }
    }

    private void onRenameNode(NodesGrid nodesGrid, NodeTO toRenameTO) {
        final WebDialog dialog = new WebDialog("Přejmenovat kategorii");
        dialog.open();

        final TextField newNameField = new TextField("Nový název:");
        newNameField.setValue(toRenameTO.getName());
        dialog.add(newNameField);

        HorizontalLayout btnLayout = new HorizontalLayout();
        dialog.addComponent(btnLayout);

        Button confirmBtn = new Button("Potvrdit", event -> {
            if (StringUtils.isBlank(newNameField.getValue())) UIUtils.showError("Název kategorie nesmí být prázdný");
            try {
                nodeService.rename(toRenameTO.getId(), newNameField.getValue());
                toRenameTO.setName(newNameField.getValue());
                populateSubNodes(nodesGrid, nodeTO.getId());
            } catch (Exception e) {
                UIUtils.showWarning("Přejmenování se nezdařilo.");
            }

            dialog.close();
        });
        btnLayout.add(confirmBtn);

        Button closeBtn = new Button("Storno", event -> dialog.close());
        btnLayout.add(closeBtn);
    }

    private void createContentsPart(Div layout, NodeTO nodeTO) {
        layout.add(new H2("Obsahy"));

        TextField searchField = new TextField();
        searchField.setPlaceholder("Název obsahu");
        searchField.setWidthFull();
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        layout.add(searchField);

        ContentsLazyGrid searchResultsTable = new ContentsLazyGrid();
        searchResultsTable.setWidthFull();
        searchResultsTable.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(searchResultsTable);

        Supplier<ContentNodeFilterTO> filterSupplier =
                () -> new ContentNodeFilterTO().setParentNodeId(nodeTO.getId()).setName(searchField.getValue());

        searchResultsTable.populate(
                q -> contentNodeService.getByFilter(filterSupplier.get(), q.getOffset(), q.getLimit()).stream(),
                q -> contentNodeService.getCountByFilter(filterSupplier.get()));

        searchField.addValueChangeListener(e -> searchResultsTable.getDataProvider().refreshAll());

        // Vytvořit obsahy
        if (coreACLService.canCreateContent(securityService.getCurrentUser())) createNewContentMenu(layout, nodeTO);
    }

    private void createNewContentMenu(Div layout, NodeTO node) {
        layout.add(new H2("Vytvořit nový obsah"));
        NewContentNodeGrid newContentsTable = new NewContentNodeGrid(node);
        layout.add(newContentsTable);
        newContentsTable.setWidthFull();
    }

    @Override
    public String getPageTitle() {
        return nodeTO.getName();
    }

}
