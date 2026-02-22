package cz.gattserver.grass.core.ui.pages.template;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.router.RouterLink;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.core.interfaces.ContentNodeTO2;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.TagPage;

import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.UserService;
import cz.gattserver.grass.core.ui.components.Breadcrumb;
import cz.gattserver.grass.core.ui.dialogs.ContentMoveDialog;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;

import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.HtmlSpan;

public class ContentViewer extends Div {

    private UserService userService;
    private SecurityService securityService;
    private CoreACLService coreACLService;
    private NodeService nodeService;

    private ContentNodeTO2 contentNodeTO;
    private H2 contentNameLabel;
    private Span contentAuthorNameLabel;
    private Span contentCreationDateNameLabel;
    private Span contentLastModificationDateLabel;
    private Div tagsListLayout;
    private Div operationsDiv;
    private Div operationsListLayout;

    private Button removeFromFavouritesButton;
    private Button addToFavouritesButton;

    private Breadcrumb breadcrumb;

    private RouterLink contentLink;

    public ContentViewer(Component contentComponent, ContentNodeTO2 contentNodeTO,
                         Consumer<ClickEvent<Button>> deleteAction, Consumer<ClickEvent<Button>> editAction,
                         RouterLink contentLink) {
        this.securityService = SpringContextHelper.getBean(SecurityService.class);
        this.userService = SpringContextHelper.getBean(UserService.class);
        this.coreACLService = SpringContextHelper.getBean(CoreACLService.class);
        this.nodeService = SpringContextHelper.getBean(NodeService.class);

        this.contentLink = contentLink;

        ComponentFactory componentFactory = new ComponentFactory();

        breadcrumb = new Breadcrumb();
        this.contentNodeTO = contentNodeTO;
        updateBreadcrumb(this.contentNodeTO);

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm:ss");

        contentNameLabel = new H2(this.contentNodeTO.name());
        contentAuthorNameLabel = new Span(this.contentNodeTO.authorName());
        contentCreationDateNameLabel = new HtmlSpan(this.contentNodeTO.creationDate() == null ? "" :
                this.contentNodeTO.creationDate().format(dateFormat));
        contentLastModificationDateLabel = new HtmlSpan(
                this.contentNodeTO.lastModificationDate() == null ? "<em>-neupraveno-</em>" :
                        dateFormat.format(this.contentNodeTO.lastModificationDate()));

        tagsListLayout = new Div();
        tagsListLayout.setId("content-info-tags");
        for (ContentTagTO contentTag : this.contentNodeTO.contentTags()) {
            RouterLink tagLink = new RouterLink(contentTag.getName(), TagPage.class,
                    URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()));
            tagsListLayout.add(new Div(tagLink));
        }

        operationsListLayout = componentFactory.createButtonLayout();
        if (!this.contentNodeTO.draft()) createContentOperations(operationsListLayout, editAction, deleteAction);

        Div leftColumnLayout = componentFactory.createLeftColumnLayout();
        add(leftColumnLayout);
        createLeftColumnContent(leftColumnLayout);

        Div rightColumnLayout = componentFactory.createRightColumnLayout();
        add(rightColumnLayout);
        createRightColumnContent(rightColumnLayout, contentComponent);

        UI.getCurrent().getPage()
                .executeJs("var pageScroll = document.getElementsByClassName('v-ui v-scrollable')" + "[0]; "
                        /*	*/ + "$(pageScroll).scroll(function() { "
                        /*		*/ + "var height = $(pageScroll).scrollTop(); "
                        /*		*/ + "if (height > 100) "
                        /*			*/ +
                        "document.getElementById('left').style['margin-top'] = (height - 100) + 'px'; "
                        /*		*/ + "else "
                        /*			*/ + "document.getElementById('left').style['margin-top'] = '0px'; "
                        /*	*/ + "});");
    }

    protected void createContentOperations(Div operationsListLayout, Consumer<ClickEvent<Button>> onEditOperation,
                                           Consumer<ClickEvent<Button>> onDeleteOperation) {
        ComponentFactory componentFactory = new ComponentFactory();

        // Upravit
        if (coreACLService.canModifyContent(contentNodeTO, securityService.getCurrentUser())) {
            Button modBtn = componentFactory.createEditButton(onEditOperation::accept);
            operationsListLayout.add(modBtn);
        }

        // Oblíbené
        removeFromFavouritesButton = componentFactory.createUnmarkFavouriteButton(event -> {
            // zdařilo se ? Pokud ano, otevři info okno
            try {
                userService.removeContentFromFavourites(contentNodeTO.contentNodeId(),
                        securityService.getCurrentUser().getId());
                removeFromFavouritesButton.setVisible(false);
                addToFavouritesButton.setVisible(true);
            } catch (Exception e) {
                // Pokud ne, otevři warn okno
                new WarnDialog("Odebrání z oblíbených se nezdařilo.").open();
            }
        });
        operationsListLayout.add(removeFromFavouritesButton);
        removeFromFavouritesButton.setVisible(
                coreACLService.canRemoveContentFromFavourites(contentNodeTO, securityService.getCurrentUser()));

        addToFavouritesButton = componentFactory.createMarkFavouriteButton(event -> {
            // zdařilo se? Pokud ano, otevři info okno
            try {
                userService.addContentToFavourites(contentNodeTO.contentNodeId(), securityService.getCurrentUser().getId());
                addToFavouritesButton.setVisible(false);
                removeFromFavouritesButton.setVisible(true);
            } catch (Exception e) {
                // Pokud ne, otevři warn okno
                new WarnDialog("Vložení do oblíbených se nezdařilo.").open();
            }
        });
        operationsListLayout.add(addToFavouritesButton);
        addToFavouritesButton.setVisible(
                coreACLService.canAddContentToFavourites(contentNodeTO, securityService.getCurrentUser()));

        // Změna kategorie
        if (coreACLService.canModifyContent(contentNodeTO, securityService.getCurrentUser())) {
            Button moveBtn = componentFactory.createMoveButton(event -> new ContentMoveDialog(contentNodeTO) {

                @Serial
                private static final long serialVersionUID = 8356571950616390549L;

                @Override
                protected void onMove() {
                    UI.getCurrent().getPage().reload();
                }
            }.open());
            operationsListLayout.add(moveBtn);
        }

        // Smazat
        if (coreACLService.canDeleteContent(contentNodeTO, securityService.getCurrentUser())) {
            Button delBtn = componentFactory.createDeleteButton(onDeleteOperation::accept);
            operationsListLayout.add(delBtn);
        }
    }

    public Div getOperationsListLayout() {
        return operationsListLayout;
    }

    private void createLeftColumnContent(Div leftContentLayout) {
        Div layout = new Div();
        layout.setClassName("left-content-view");
        leftContentLayout.add(layout);

        // info - přehled
        layout.add(new H3("Info"));
        Div info = new Div();
        info.setClassName("content-info");
        layout.add(info);

        Div authorPart = new Div();
        info.add(authorPart);
        authorPart.add(new Strong("Autor:"));
        authorPart.add(new Breakline());
        authorPart.add(contentAuthorNameLabel);

        Div createdPart = new Div();
        info.add(createdPart);
        createdPart.add(new Strong("Vytvořeno:"));
        createdPart.add(new Breakline());
        createdPart.add(contentCreationDateNameLabel);

        Div modifiedPart = new Div();
        info.add(modifiedPart);
        modifiedPart.add(new Strong("Upraveno:"));
        modifiedPart.add(new Breakline());
        modifiedPart.add(contentLastModificationDateLabel);

        if (!contentNodeTO.publicated()) {
            Div publicatedLayout = new Div();
            publicatedLayout.addClassName("not-publicated-info");
            publicatedLayout.add(ImageIcon.INFO_16_ICON.createImage("Info"));
            publicatedLayout.add("Nepublikováno");
            info.add(publicatedLayout);
        }

        // tagy
        layout.add(new H3("Tagy"));
        layout.add(tagsListLayout);

        // nástrojová lišta
        operationsDiv = new Div();
        layout.add(operationsDiv);
        H3 operationsHeader = new H3("Operace s obsahem");
        operationsDiv.add(operationsHeader);
        operationsDiv.add(operationsListLayout);
        operationsDiv.setVisible(operationsListLayout.getChildren().count() > 0);
    }

    private void createRightColumnContent(Div rightContentLayout, Component contentComponent) {
        rightContentLayout.add(breadcrumb);
        rightContentLayout.add(contentNameLabel);

        // samotný obsah
        rightContentLayout.add(contentComponent);
    }


    private void updateBreadcrumb(ContentNodeTO2 content) {

        // pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
        // aktuální polohu cílové kategorie
        List<Breadcrumb.BreadcrumbElement> breadcrumbElements = new ArrayList<>();

        /**
         * obsah
         */
        breadcrumbElements.add(new Breadcrumb.BreadcrumbElement(contentLink));

        /**
         * kategorie
         */
        NodeTO parent = nodeService.getNodeByIdForDetail(content.parentId());
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
}