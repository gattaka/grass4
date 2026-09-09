package cz.gattserver.grass.core.ui.pages;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.ui.CopyTextDialog;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.modules.ContentModule;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.dialogs.MoveIntoNodeDialog;
import cz.gattserver.grass.core.ui.pages.factories.NodePageFactory;
import jakarta.annotation.Nullable;
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

    private final NodePageFactory nodePageFactory;

    private NodeTO nodeTO;
    private boolean explicitAccess;

    public NodePage(ContentNodeService contentNodeService, NodeService nodeService, CoreACLService coreACLService,
                    SecurityService securityService, NodePageFactory nodePageFactory) {
        this.contentNodeService = contentNodeService;
        this.nodeService = nodeService;
        this.coreACLService = coreACLService;
        this.securityService = securityService;
        this.nodePageFactory = nodePageFactory;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
        if (identifier == null) throw new GrassPageException(404);

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        nodeTO = nodeService.getNodeById(identifier.id(), identifier.hash());
        if (nodeTO == null) throw new GrassPageException(404);

        explicitAccess = identifier.hash() != null;

        // Navigační breadcrumb
        createBreadcrumb(layout);

        // Podkategorie
        createSubnodesPart(layout);

        // Obsahy
        createContentsPart(layout);
    }

    private void createBreadcrumb(Div layout) {
        if (explicitAccess) return;

        Breadcrumb breadcrumb = new Breadcrumb();
        layout.add(breadcrumb);

        // pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
        // aktuální polohu cílové kategorie
        List<Breadcrumb.BreadcrumbElement> breadcrumbElements = new ArrayList<>();
        NodeTO parent = nodeTO;
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

    private void populateSubNodes(NodesGrid nodesGrid) {
        List<NodeTO> nodes = nodeService.getNodesByParentNode(nodeTO.getId(), explicitAccess);
        if (nodes == null) throw new GrassPageException(500);
        nodesGrid.populate(nodes);
    }

    private void createSubnodesPart(Div layout) {
        layout.add(new H2("Podkategorie"));

        boolean admin = securityService.getCurrentUser().isAdmin();
        NodesGrid nodesGrid = new NodesGrid(admin, explicitAccess);
        populateSubNodes(nodesGrid);

        layout.add(nodesGrid);
        nodesGrid.setWidthFull();

        if (admin) {
            nodesGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

            ComponentFactory componentFactory = new ComponentFactory();

            Div buttonLayout = componentFactory.createButtonLayout();
            buttonLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
            layout.add(buttonLayout);

            Button createButton = componentFactory.createCreateButton(e -> onCreateNode(nodesGrid, nodeTO));
            buttonLayout.add(createButton);

            Button editButton =
                    componentFactory.createEditGridButton(toRenameTO -> onEditNode(nodesGrid, nodeTO, toRenameTO),
                            nodesGrid);
            buttonLayout.add(editButton);

            Button moveBtn = componentFactory.createMoveGridButton(set -> {
                NodeTO toMoveTO = set.iterator().next();
                new MoveIntoNodeDialog(toMoveTO.getParentId(),
                        newParentTO -> onMoveAction(nodesGrid, toMoveTO, newParentTO)).open();
            }, nodesGrid);
            buttonLayout.add(moveBtn);

            Button linkBtn = componentFactory.createExplicitLinkGridButton(toLinkTO -> {
                String explicitAccessHash = nodeService.createExplicitAccessHash(toLinkTO.getId());
                String link = URLIdentifierUtils.createURLIdentifier(toLinkTO.getId(), toLinkTO.getName(),
                        explicitAccessHash);
                String url = UIUtils.getPageURL(nodePageFactory, link);
                new CopyTextDialog(UIUtils.getURLBase() + url).open();
            }, nodesGrid);
            buttonLayout.add(linkBtn);

            buttonLayout.add(
                    componentFactory.createDeleteGridButton(toDeleteTO -> onDeleteNode(nodesGrid, toDeleteTO.getId()),
                            nodesGrid));
        }
    }

    public void onCreateNode(NodesGrid nodesGrid, NodeTO parentNodeTO) {
        createNodeDialog(nodesGrid, parentNodeTO, null);
    }

    public void onEditNode(NodesGrid nodesGrid, NodeTO parentNodeTO, NodeTO currentNodeTO) {
        createNodeDialog(nodesGrid, parentNodeTO, currentNodeTO);
    }

    private void createNodeDialog(NodesGrid nodesGrid, NodeTO parentNodeTO, @Nullable NodeTO currentNodeTO) {
        final WebDialog dialog = new WebDialog(currentNodeTO == null ? "Vytvořit kategorii" : "Upravit kategorii");
        dialog.setWidth(350, Unit.PIXELS);

        final TextField newNameField = new TextField();
        newNameField.setPlaceholder("Název kategorie");
        newNameField.setWidthFull();
        dialog.addComponent(newNameField);

        final Checkbox hiddenCheckbox = new Checkbox("Skrytá kategorie");
        dialog.addComponent(hiddenCheckbox);

        NodeTO to = currentNodeTO == null ? new NodeTO() : currentNodeTO.copy();
        to.setParentId(parentNodeTO.getId());

        Binder<NodeTO> binder = new Binder<>(NodeTO.class);
        binder.forField(newNameField).withValidator(StringUtils::isNotBlank, "Název kategorie nesmí být prázdný")
                .bind(NodeTO::getName, NodeTO::setName);
        binder.forField(hiddenCheckbox).bind(NodeTO::getHidden, NodeTO::setHidden);
        binder.setBean(to);

        ComponentFactory componentFactory = new ComponentFactory();
        dialog.getFooter().add(componentFactory.createDialogSubmitOrStornoLayout(event -> {
            if (binder.validate().isOk()) {
                Long newNodeId = nodeService.save(to);
                if (currentNodeTO == null) {
                    UI.getCurrent()
                            .navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(newNodeId, to.getName()));
                } else {
                    populateSubNodes(nodesGrid);
                }
                dialog.close();
            }
        }, event -> dialog.close()));

        dialog.open();
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
                populateSubNodes(nodesGrid);
            } catch (IllegalArgumentException ex) {
                UIUtils.showWarning("Nelze přesunou předka do potomka");
            }
        }).open();
    }

    private void onDeleteNode(NodesGrid nodesGrid, Long nodeId) {
        if (!nodeService.isNodeEmpty(nodeId)) {
            UIUtils.showWarning("Kategorie musí být prázdná");
        } else {
            nodeService.deleteNode(nodeId);
            populateSubNodes(nodesGrid);
        }
    }

    private void createContentsPart(Div layout) {
        layout.add(new H2("Obsahy"));

        TextField searchField = new TextField();
        searchField.setPlaceholder("Název obsahu");
        searchField.setWidthFull();
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        layout.add(searchField);

        ContentsLazyGrid searchResultsTable = new ContentsLazyGrid(true, explicitAccess);
        searchResultsTable.setWidthFull();
        searchResultsTable.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(searchResultsTable);

        Supplier<ContentNodeFilterTO> filterSupplier =
                () -> new ContentNodeFilterTO().setParentNodeId(nodeTO.getId()).setName(searchField.getValue());

        searchResultsTable.populate(
                q -> contentNodeService.getByFilter(filterSupplier.get(), explicitAccess, q.getOffset(), q.getLimit())
                        .stream(), q -> contentNodeService.getCountByFilter(filterSupplier.get(), explicitAccess));

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