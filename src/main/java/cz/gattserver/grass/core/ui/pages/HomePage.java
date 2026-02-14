package cz.gattserver.grass.core.ui.pages;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;
import cz.gattserver.common.exception.ApplicationErrorHandler;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.services.*;
import cz.gattserver.grass.core.ui.components.NodesGrid;

import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import cz.gattserver.common.server.URLIdentifierUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import cz.gattserver.grass.core.ui.util.UIUtils;

@Route(value = "", layout = MainView.class)
@RouteAlias("home")
@PageTitle("Gattserver")
public class HomePage extends Div {

    // Pro scan komponent do NPM
    // https://vaadin.com/forum/thread/17765421/17771093
    @SuppressWarnings("unused")
    private TreeGrid<?> treeGridDeclaration;

    /**
     * Kolik je nejmenší font pro tagcloud ?
     */
    private static final int MIN_FONT_SIZE_TAG_CLOUD = 8;

    /**
     * Kolik je největší font pro tagcloud ?
     */
    private static final int MAX_FONT_SIZE_TAG_CLOUD = 22;

    private ContentTagService contentTagService;
    private ContentNodeService contentNodeService;

    private UserInfoTO user;

    private TextField searchField;
    @Autowired
    private NodeService nodeService;

    public HomePage(SecurityService securityService, CoreACLService coreACLService, ContentTagService contentTagService,
                    ContentNodeService contentNodeService) {
        this.contentNodeService = contentNodeService;
        this.contentTagService = contentTagService;

        VaadinSession.getCurrent().setErrorHandler(new ApplicationErrorHandler());

        ComponentFactory componentFactory = new ComponentFactory();
        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        // Oblíbené
        user = securityService.getCurrentUser();
        if (coreACLService.isLoggedIn(user)) {
            layout.add(new H2("Oblíbené obsahy"));
            ContentsLazyGrid favouritesContentsGrid = new ContentsLazyGrid();
            favouritesContentsGrid.populate(user.getId() != null,
                    q -> contentNodeService.getUserFavourite(user.getId(), q.getOffset(), q.getLimit()).stream(),
                    q -> contentNodeService.getUserFavouriteCount(user.getId()));
            layout.add(favouritesContentsGrid);
            favouritesContentsGrid.setWidthFull();
        }

        createSearchMenu(layout);

        // Nedávno přidané a upravené obsahy
        createRecentAdded(layout);
        createRecentModified(layout);

        // Tag-cloud
        Div tagJsDiv = new Div() {

            @Serial
            private static final long serialVersionUID = -5913404248739828949L;

            @ClientCallable
            private void tagCloundCallback() {
                createTagCloud(layout);
            }
        };

        String tagJsDivId = "tag-js-div";
        tagJsDiv.setId(tagJsDivId);
        layout.add(tagJsDiv);

        UI.getCurrent().getPage().executeJs("setTimeout(function(){ document.getElementById('" + tagJsDivId +
                "').$server.tagCloundCallback() }, 10);");
    }

    private ContentNodeFilterTO createFilterTO() {
        return new ContentNodeFilterTO().setName(searchField.getValue());
    }

    private void createSearchMenu(Div layout) {
        layout.add(new H2("Vyhledávání"));

        searchField = new TextField();
        searchField.setPlaceholder("Název obsahu");
        searchField.setWidthFull();
        layout.add(searchField);

        final ContentsLazyGrid searchResultsContentsGrid = new ContentsLazyGrid();
        searchResultsContentsGrid.setWidthFull();
        searchResultsContentsGrid.setVisible(false);
        searchResultsContentsGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(searchResultsContentsGrid);

        final NodesGrid searchResultsNodesGrid = new NodesGrid();
        searchResultsNodesGrid.setWidthFull();
        searchResultsNodesGrid.setVisible(false);
        searchResultsNodesGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(searchResultsNodesGrid);

        searchField.addValueChangeListener(e -> {
            String value = searchField.getValue();
            if (StringUtils.isNotBlank(value) && !searchResultsContentsGrid.isVisible()) {
                searchResultsContentsGrid.setVisible(true);
                searchResultsContentsGrid.populate(user.getId() != null,
                        q -> contentNodeService.getByFilter(createFilterTO(), q.getOffset(), q.getLimit()).stream(),
                        q -> contentNodeService.getCountByFilter(createFilterTO()));
                searchResultsContentsGrid.setHeight("200px");

                searchResultsNodesGrid.setVisible(true);
            }
            searchResultsContentsGrid.getDataProvider().refreshAll();
            searchResultsNodesGrid.populate(nodeService.getByFilter(value));
            searchResultsNodesGrid.setHeight("200px");
        });
        searchField.setValueChangeMode(ValueChangeMode.EAGER);
    }

    private void createTagCloud(Div layout) {
        layout.add(new H2("Tagy"));

        List<ContentTagsCloudItemTO> contentTags =
                contentTagService.createTagsCloud(MAX_FONT_SIZE_TAG_CLOUD, MIN_FONT_SIZE_TAG_CLOUD);
        if (contentTags.isEmpty()) {
            Span noTagsSpan = new Span("Nebyly nalezeny žádné tagy");
            layout.add(noTagsSpan);
            return;
        }

        ComponentFactory componentFactory = new ComponentFactory();
        Div tagsMenu = componentFactory.createButtonLayout();
        tagsMenu.setId("tag-menu");
        tagsMenu.setWidthFull();
        layout.add(tagsMenu);

        char oldChar = 0;
        char currChar = 0;
        List<RouterLink> tagLinks = new ArrayList<>();
        for (ContentTagsCloudItemTO contentTag : contentTags) {
            currChar = contentTag.getName().toUpperCase().charAt(0);
            if (currChar != oldChar || oldChar == 0) {
                if (oldChar != 0) populateTags(tagLinks, oldChar, layout, tagsMenu);
                tagLinks = new ArrayList<>();
                oldChar = currChar;
            }

            RouterLink tagLink = new RouterLink(TagPage.class,
                    URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()));
            tagLink.setText(contentTag.getName());
            tagLink.getElement().setAttribute("title", contentTag.getContentsCount().toString());
            tagLink.getStyle().set("font-size", contentTag.getFontSize() + "pt");
            tagLinks.add(tagLink);
        }
        if (!tagLinks.isEmpty()) populateTags(tagLinks, currChar, layout, tagsMenu);
    }

    private void populateTags(List<RouterLink> tagLinks, char tag, Div tagCloudLayout, Div tagsMenu) {
        Div tagBlock = new Div();
        tagCloudLayout.add(tagBlock);

        Div tagLetter = new Div();
        String tagString = String.valueOf(tag);
        tagLetter.addClassName("tag-letter");
        tagLetter.setId("tag-" + tagString);
        tagLetter.add(tagString);
        tagBlock.add(tagLetter);
        tagLetter.setSizeUndefined();

        Button anchorButton = new Button(tagString);
        anchorButton.getStyle().set("min-width", "auto");
        tagsMenu.add(new Anchor("#tag-" + tagString, anchorButton));

        Div tagLabels = new Div();
        tagLabels.addClassName("tag-labels");
        tagBlock.add(tagLabels);

        for (RouterLink tagLink : tagLinks)
            tagLabels.add(tagLink);
    }

    private void createRecentAdded(Div layout) {
        layout.add(new H2("Nedávno přidané obsahy"));

        ContentsLazyGrid recentAddedContentsGrid = new ContentsLazyGrid();
        recentAddedContentsGrid.populate(user.getId() != null,
                q -> contentNodeService.getRecentAdded(q.getOffset(), q.getLimit()).stream(),
                q -> contentNodeService.getCount());
        recentAddedContentsGrid.setWidthFull();
        recentAddedContentsGrid.setHeight("200px");
        layout.add(recentAddedContentsGrid);
    }

    private void createRecentModified(Div layout) {
        layout.add(new H2("Nedávno upravené obsahy"));

        ContentsLazyGrid recentModifiedContentsGrid = new ContentsLazyGrid();
        recentModifiedContentsGrid.populate(user.getId() != null,
                q -> contentNodeService.getRecentModified(q.getOffset(), q.getLimit()).stream(),
                q -> contentNodeService.getCount());
        recentModifiedContentsGrid.setWidthFull();
        recentModifiedContentsGrid.setHeight("200px");
        layout.add(recentModifiedContentsGrid);
    }
}