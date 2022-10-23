package cz.gattserver.grass.core.ui.pages;

import java.util.List;

import javax.annotation.Resource;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import cz.gattserver.grass.core.interfaces.ContentNodeFilterTO;
import cz.gattserver.grass.core.interfaces.ContentTagsCloudItemTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.ui.components.ContentsLazyGrid;
import cz.gattserver.web.common.server.URLIdentifierUtils;
import cz.gattserver.web.common.ui.HtmlSpan;
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
@JsModule("custom.js")
@Theme(value = Lumo.class)
@CssImport("styles.css")
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
	private ContentTagService contentTagFacade;

	@Autowired
	private ContentNodeService contentNodeFacade;

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
			ContentsLazyGrid favouritesContentsTable = new ContentsLazyGrid();
			favouritesContentsTable.populate(getUser().getId() != null, this,
					q -> contentNodeFacade.getUserFavourite(user.getId(), q.getOffset(), q.getLimit()).stream(),
					q -> contentNodeFacade.getUserFavouriteCount(user.getId()));
			layout.add(favouritesContentsTable);
			favouritesContentsTable.setWidthFull();
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

		final ContentsLazyGrid searchResultsTable = new ContentsLazyGrid();
		searchResultsTable.setWidthFull();
		searchResultsTable.setVisible(false);
		searchResultsTable.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		layout.add(searchResultsTable);

		searchField.addValueChangeListener(e -> {
			String value = searchField.getValue();
			if (StringUtils.isNotBlank(value) && !searchResultsTable.isVisible()) {
				searchResultsTable.setVisible(true);
				// zde musí být searchField.getValue() namísto pouze value,
				// protože jde o closure a bude se vyhodnocovat opakovaně
				// později s různými hodnotami obsahu pole
				searchResultsTable.populate(getUser().getId() != null, HomePage.this,
						q -> contentNodeFacade.getByFilter(createFilterTO(), q.getOffset(), q.getLimit()).stream(),
						q -> contentNodeFacade.getCountByFilter(createFilterTO()));
				searchResultsTable.setHeight("200px");
			}
			searchResultsTable.getDataProvider().refreshAll();
		});
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
	}

	private void createTagCloud(Div layout) {
		layout.add(new H2("Tagy"));

		List<ContentTagsCloudItemTO> contentTags = contentTagFacade.createTagsCloud(MAX_FONT_SIZE_TAG_CLOUD,
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

		ContentsLazyGrid recentAddedContentsTable = new ContentsLazyGrid();
		recentAddedContentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getRecentAdded(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCount());
		recentAddedContentsTable.setWidthFull();
		recentAddedContentsTable.setHeight("200px");
		layout.add(recentAddedContentsTable);
	}

	private void createRecentModified(Div layout) {
		layout.add(new H2("Nedávno upravené obsahy"));

		ContentsLazyGrid recentModifiedContentsTable = new ContentsLazyGrid();
		recentModifiedContentsTable.populate(getUser().getId() != null, this,
				q -> contentNodeFacade.getRecentModified(q.getOffset(), q.getLimit()).stream(),
				q -> contentNodeFacade.getCount());
		recentModifiedContentsTable.setWidthFull();
		recentModifiedContentsTable.setHeight("200px");
		layout.add(recentModifiedContentsTable);
	}

}
