package cz.gattserver.grass.core.ui.pages;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.component.UI;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import org.apache.commons.lang3.StringUtils;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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

    private static final long serialVersionUID = 1560125362904332256L;

    private ContentNodeService contentNodeService;
    private NodeService nodeService;
    private CoreACLService coreACLService;
    private SecurityService securityService;

    private TextField searchField;

    private NodeTO node;

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

        node = nodeService.getNodeByIdForDetail(identifier.getId());

        // Navigační breadcrumb
        createBreadcrumb(layout, node);

        // Podkategorie
        createSubnodesPart(layout, node);

        // Obsahy
        createContentsPart(layout, node);
    }

    private void createNewNodePanel(Div layout, final NodeTO node) {
        ComponentFactory componentFactory = new ComponentFactory();
        Div buttonLayout = componentFactory.createButtonLayout();
        buttonLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(buttonLayout);
        Button createButton = componentFactory.createCreateButton(e -> createNodeAction(node));
        buttonLayout.add(createButton);
    }

    public void createNodeAction(NodeOverviewTO parentNode) {
        final WebDialog dialog = new WebDialog("Vytvořit kategorii");

        final TextField newNameField = new TextField();
        newNameField.setPlaceholder("Nová kategorie do " + parentNode.getName());
        newNameField.setWidthFull();
        dialog.addComponent(newNameField);

        NodeOverviewTO to = new NodeOverviewTO();
        Binder<NodeOverviewTO> binder = new Binder<>(NodeOverviewTO.class);
        binder.forField(newNameField).withValidator(StringUtils::isNotBlank, "Název kategorie nesmí být prázdný")
                .bind(NodeOverviewTO::getName, NodeOverviewTO::setName);
        binder.setBean(to);

        ComponentFactory componentFactory = new ComponentFactory();
        HorizontalLayout saveCloseLayout = componentFactory.createDialogSubmitOrStornoLayout(event -> {
            if (binder.validate().isOk()) {
                Long newNodeId = nodeService.createNewNode(parentNode.getId(), to.getName());
                UI.getCurrent()
                        .navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(newNodeId, to.getName()));
                dialog.close();
            }
        }, event -> dialog.close());
        dialog.add(saveCloseLayout);
        dialog.setWidth("300px");

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
            if (parent.getParent() == null) break;

            parent = parent.getParent();
        }

        breadcrumb.resetBreadcrumb(breadcrumbElements);
    }

    private void createSubnodesPart(Div layout, NodeTO node) {
        layout.add(new H2("Podkategorie"));

        List<NodeOverviewTO> nodes = nodeService.getNodesByParentNode(node.getId());
        if (nodes == null) throw new GrassPageException(500);
        NodesGrid subNodesTable = new NodesGrid();
        subNodesTable.populate(nodes);

        layout.add(subNodesTable);
        subNodesTable.setWidthFull();

        // Vytvořit novou kategorii
        if (coreACLService.canCreateNode(securityService.getCurrentUser())) createNewNodePanel(layout, node);
    }

    private ContentNodeFilterTO createFilterTO() {
        return new ContentNodeFilterTO().setParentNodeId(node.getId()).setName(searchField.getValue());
    }

    private void createContentsPart(Div layout, NodeTO node) {
        layout.add(new H2("Obsahy"));

        searchField = new TextField();
        searchField.setPlaceholder("Název obsahu");
        searchField.setWidthFull();
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
        layout.add(searchField);

        ContentsLazyGrid searchResultsTable = new ContentsLazyGrid();
        searchResultsTable.setWidthFull();
        searchResultsTable.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(searchResultsTable);

        searchResultsTable.populate(securityService.getCurrentUser().getId() != null,
                q -> contentNodeService.getByFilter(createFilterTO(), q.getOffset(), q.getLimit()).stream(),
                q -> contentNodeService.getCountByFilter(createFilterTO()));

        searchField.addValueChangeListener(e -> searchResultsTable.getDataProvider().refreshAll());

        // Vytvořit obsahy
        if (coreACLService.canCreateContent(securityService.getCurrentUser())) createNewContentMenu(layout, node);
    }

    private void createNewContentMenu(Div layout, NodeTO node) {
        layout.add(new H2("Vytvořit nový obsah"));
        NewContentNodeGrid newContentsTable = new NewContentNodeGrid(node);
        layout.add(newContentsTable);
        newContentsTable.setWidthFull();
    }

    @Override
    public String getPageTitle() {
        return node.getName();
    }

}
