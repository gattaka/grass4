package cz.gattserver.grass.core.ui.pages;

import java.util.List;

import cz.gattserver.grass.core.ui.components.NodesGrid;
import jakarta.annotation.Resource;

import com.vaadin.flow.component.dependency.JsModule;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlSpan;
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

import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;

@Route("")
@RouteAlias("home")
@JsModule("themes/grass/custom.js")
@PageTitle("Gattserver")
public class HomePage extends OneColumnPage {

	private static final long serialVersionUID = 3100924667157515504L;

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

	@Autowired
	private ContentTagService contentTagService;

	@Autowired
	private ContentNodeService contentNodeService;

	@Resource(name = "tagPageFactory")
	private PageFactory tagPageFactory;

	private TextField searchField;

	public HomePage() {
		init();
	}

	@Override
	protected void createColumnContent(Div layout) {

		// Oblíbené
		UserInfoTO user = getUser();
		if (coreACL.isLoggedIn(user)) {
			layout.add(new H2("Oblíbené obsahy"));
			ContentsLazyGrid favouritesContentsGrid = new ContentsLazyGrid();
			favouritesContentsGrid.populate(getUser().getId() != null,
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
			private static final long serialVersionUID = -7319482130016598549L;

			@ClientCallable
			private void tagCloundCallback() {
				createTagCloud(layout);
			}
		};

		String tagJsDivId = "tag-js-div";
		tagJsDiv.setId(tagJsDivId);
		layout.add(tagJsDiv);

		UI.getCurrent().getPage().executeJs("setTimeout(function(){ document.getElementById('" + tagJsDivId
				+ "').$server.tagCloundCallback() }, 10);");
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
				searchResultsContentsGrid.populate(getUser().getId() != null,
						q -> contentNodeService.getByFilter(createFilterTO(), q.getOffset(), q.getLimit()).stream(),
						q -> contentNodeService.getCountByFilter(createFilterTO()));
				searchResultsContentsGrid.setHeight("200px");

				searchResultsNodesGrid.setVisible(true);
			}
			searchResultsContentsGrid.getDataProvider().refreshAll();
			searchResultsNodesGrid.populate(nodeFacade.getByFilter(value));
			searchResultsNodesGrid.setHeight("200px");
		});
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
	}

	private void createTagCloud(Div layout) {
		layout.add(new H2("Tagy"));

		List<ContentTagsCloudItemTO> contentTags = contentTagService.createTagsCloud(MAX_FONT_SIZE_TAG_CLOUD,
				MIN_FONT_SIZE_TAG_CLOUD);
		if (contentTags.isEmpty()) {
			Span noTagsSpan = new Span("Nebyly nalezeny žádné tagy");
			layout.add(noTagsSpan);
			return;
		}

		ButtonLayout tagsMenu = new ButtonLayout();
		tagsMenu.setWidthFull();
		tagsMenu.getStyle().set("text-align", "center");
		layout.add(tagsMenu);

		char oldChar = 0;
		char currChar = 0;
		StringBuilder sb = null;
		for (ContentTagsCloudItemTO contentTag : contentTags) {
			currChar = contentTag.getName().toUpperCase().charAt(0);
			if (currChar != oldChar || oldChar == 0) {
				if (oldChar != 0)
					populateTags(sb, oldChar, layout, tagsMenu);
				sb = new StringBuilder();
				oldChar = currChar;
			}

			sb.append("<a title='" + contentTag.getContentsCount() + "'href='"
					+ getPageURL(tagPageFactory,
					URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName()))
					+ "' style='font-size:" + contentTag.getFontSize() + "pt'>" + contentTag.getName() + "</a> ");
		}
		if (sb != null)
			populateTags(sb, currChar, layout, tagsMenu);
	}

	private void populateTags(StringBuilder sb, char tag, Div tagCloudLayout, ButtonLayout tagsMenu) {
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

		Span tags = new HtmlSpan(sb.toString());
		tagLabels.add(tags);
		tags.setSizeFull();
	}

	private void createRecentAdded(Div layout) {
		layout.add(new H2("Nedávno přidané obsahy"));

		ContentsLazyGrid recentAddedContentsGrid = new ContentsLazyGrid();
		recentAddedContentsGrid.populate(getUser().getId() != null,
				q -> contentNodeService.getRecentAdded(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeService.getCount());
		recentAddedContentsGrid.setWidthFull();
		recentAddedContentsGrid.setHeight("200px");
		layout.add(recentAddedContentsGrid);
	}

	private void createRecentModified(Div layout) {
		layout.add(new H2("Nedávno upravené obsahy"));

		ContentsLazyGrid recentModifiedContentsGrid = new ContentsLazyGrid();
		recentModifiedContentsGrid.populate(getUser().getId() != null,
				q -> contentNodeService.getRecentModified(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeService.getCount());
		recentModifiedContentsGrid.setWidthFull();
		recentModifiedContentsGrid.setHeight("200px");
		layout.add(recentModifiedContentsGrid);
	}
}