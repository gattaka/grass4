package cz.gattserver.grass.core.ui.pages.template;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeTO;
import cz.gattserver.grass.core.services.UserService;
import cz.gattserver.grass.core.ui.components.Breadcrumb;
import cz.gattserver.grass.core.ui.components.button.DeleteButton;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.components.button.ModifyButton;
import cz.gattserver.grass.core.ui.dialogs.ContentMoveDialog;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;

import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.Strong;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.HtmlSpan;

public abstract class ContentViewerPage extends TwoColumnPage {

	private static final long serialVersionUID = -1564043277444025560L;

	@Autowired
	protected UserService userFacade;

	@Resource(name = "tagPageFactory")
	protected PageFactory tagPageFactory;

	private ContentNodeTO content;
	private H2 contentNameLabel;
	private Span contentAuthorNameLabel;
	private Span contentCreationDateNameLabel;
	private Span contentLastModificationDateLabel;
	private Div tagsListLayout;
	private Div operationsDiv;
	private Div operationsListLayout;

	private ImageButton removeFromFavouritesButton;
	private ImageButton addToFavouritesButton;

	/**
	 * Breadcrumb
	 */
	private Breadcrumb breadcrumb;

	@Override
	public void init() {
		breadcrumb = new Breadcrumb();

		content = getContentNodeDTO();
		updateBreadcrumb(content);

		DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d.M.yyyy HH:mm:ss");

		contentNameLabel = new H2(content.getName());
		contentAuthorNameLabel = new Span(content.getAuthor().getName());
		contentCreationDateNameLabel = new HtmlSpan(
				content.getCreationDate() == null ? "" : content.getCreationDate().format(dateFormat));
		contentLastModificationDateLabel = new HtmlSpan(
				content.getLastModificationDate() == null ? "<em>-neupraveno-</em>"
						: dateFormat.format(content.getLastModificationDate()));

		tagsListLayout = new Div();
		tagsListLayout.setId("content-info-tags");
		for (ContentTagOverviewTO contentTag : content.getContentTags()) {
			Anchor tagLink = new Anchor(
					getPageURL(tagPageFactory,
							URLIdentifierUtils.createURLIdentifier(contentTag.getId(), contentTag.getName())),
					contentTag.getName());
			tagsListLayout.add(new Div(tagLink));
		}

		operationsListLayout = new ButtonLayout();
		if (!content.isDraft())
			createContentOperations(operationsListLayout);

		UI.getCurrent().getPage().executeJs("var pageScroll = document.getElementsByClassName('v-ui v-scrollable')" +
				"[0]; "
				/*	*/ + "$(pageScroll).scroll(function() { "
				/*		*/ + "var height = $(pageScroll).scrollTop(); "
				/*		*/ + "if (height > 100) "
				/*			*/ + "document.getElementById('left').style['margin-top'] = (height - 100) + 'px'; "
				/*		*/ + "else "
				/*			*/ + "document.getElementById('left').style['margin-top'] = '0px'; "
				/*	*/ + "});");

		super.init();
	}

	protected void createContentOperations(Div operationsListLayout) {
		// Upravit
		if (coreACL.canModifyContent(content, getUser())) {
			ModifyButton modBtn = new ModifyButton("Upravit", event -> onEditOperation());
			operationsListLayout.add(modBtn);
		}

		// Smazat
		if (coreACL.canDeleteContent(content, getUser())) {
			DeleteButton delBtn = new DeleteButton("Smazat", event -> onDeleteOperation());
			operationsListLayout.add(delBtn);
		}

		// Oblíbené
		removeFromFavouritesButton = new ImageButton("Odebrat z oblíbených", ImageIcon.BROKEN_HEART_16_ICON,
				event -> {
					// zdařilo se ? Pokud ano, otevři info okno
					try {
						userFacade.removeContentFromFavourites(content.getId(), getUser().getId());
						removeFromFavouritesButton.setVisible(false);
						addToFavouritesButton.setVisible(true);
					} catch (Exception e) {
						// Pokud ne, otevři warn okno
						new WarnDialog("Odebrání z oblíbených se nezdařilo.").open();
					}
				});
		operationsListLayout.add(removeFromFavouritesButton);
		removeFromFavouritesButton.setVisible(coreACL.canRemoveContentFromFavourites(content, getUser()));

		addToFavouritesButton = new ImageButton("Přidat do oblíbených", ImageIcon.HEART_16_ICON, event -> {
			// zdařilo se? Pokud ano, otevři info okno
			try {
				userFacade.addContentToFavourites(content.getId(), getUser().getId());
				addToFavouritesButton.setVisible(false);
				removeFromFavouritesButton.setVisible(true);
			} catch (Exception e) {
				// Pokud ne, otevři warn okno
				new WarnDialog("Vložení do oblíbených se nezdařilo.").open();
			}
		});
		operationsListLayout.add(addToFavouritesButton);
		addToFavouritesButton.setVisible(coreACL.canAddContentToFavourites(content, getUser()));

		// Změna kategorie
		if (coreACL.canModifyContent(content, getUser())) {
			ImageButton moveBtn = new ImageButton("Přesunout", ImageIcon.MOVE_16_ICON,
					event -> new ContentMoveDialog(content) {
						private static final long serialVersionUID = 3748723613020816248L;

						@Override
						protected void onMove() {
							UIUtils.redirect(getPageURL(getContentViewerPageFactory(),
									URLIdentifierUtils.createURLIdentifier(content.getContentID(),
											content.getName())));
						}
					}.open());
			operationsListLayout.add(moveBtn);
		}
	}

	@Override
	protected void createLeftColumnContent(Div leftContentLayout) {
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

		if (!content.isPublicated()) {
			Div publicatedLayout = new Div();
			publicatedLayout.addClassName("not-publicated-info");
			publicatedLayout.add(new Image(ImageIcon.INFO_16_ICON.createResource(), "Info"));
			publicatedLayout.add(new Strong("Nepublikováno"));
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

	@Override
	protected void createRightColumnContent(Div rightContentLayout) {
		rightContentLayout.add(breadcrumb);
		rightContentLayout.add(contentNameLabel);

		// samotný obsah
		createContent(rightContentLayout);
	}

	protected abstract void createContent(Div layout);

	protected abstract ContentNodeTO getContentNodeDTO();

	protected abstract PageFactory getContentViewerPageFactory();

	private void updateBreadcrumb(ContentNodeTO content) {

		// pokud zjistím, že cesta neodpovídá, vyhodím 302 (přesměrování) na
		// aktuální polohu cílové kategorie
		List<Breadcrumb.BreadcrumbElement> breadcrumbElements = new ArrayList<>();

		/**
		 * obsah
		 */
		breadcrumbElements.add(new Breadcrumb.BreadcrumbElement(content.getName(),
				getPageURL(getContentViewerPageFactory(),
						URLIdentifierUtils.createURLIdentifier(content.getContentID(), content.getName()))));

		/**
		 * kategorie
		 */
		NodeTO parent = nodeFacade.getNodeByIdForDetail(content.getParent().getId());
		while (true) {

			// nejprve zkus zjistit, zda předek existuje
			if (parent == null)
				throw new GrassPageException(404);

			breadcrumbElements.add(new Breadcrumb.BreadcrumbElement(parent.getName(), getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(parent.getId(), parent.getName()))));

			// pokud je můj předek null, pak je to konec a je to všechno
			if (parent.getParent() == null)
				break;

			parent = parent.getParent();
		}

		breadcrumb.resetBreadcrumb(breadcrumbElements);
	}

	protected abstract void onDeleteOperation();

	protected abstract void onEditOperation();
}
