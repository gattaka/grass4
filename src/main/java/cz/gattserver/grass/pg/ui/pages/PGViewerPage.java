package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.StreamResource;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.LinkButton;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.pg.config.PGConfiguration;
import cz.gattserver.grass.pg.events.impl.*;
import cz.gattserver.grass.pg.interfaces.PhotogalleryItemType;
import cz.gattserver.grass.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.ContentViewerPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.pg.util.PGUtils;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

@Route("photogallery")
public class PGViewerPage extends ContentViewerPage implements HasUrlParameter<String>, HasDynamicTitle {

	private static final long serialVersionUID = 7334408385869747381L;

	private static final Logger logger = LoggerFactory.getLogger(PGViewerPage.class);

	private static final int MAX_PAGE_RADIUS = 2;
	private static final int PAGE_SIZE = 12;

	private static final String MAGICK_WORD = "MAG1CK";

	@Autowired
	private PGService pgService;

	@Resource(name = "pgViewerPageFactory")
	private PageFactory photogalleryViewerPageFactory;

	@Resource(name = "pgEditorPageFactory")
	private PageFactory photogalleryEditorPageFactory;

	@Autowired
	private EventBus eventBus;

	private ProgressDialog progressIndicatorWindow;

	private PhotogalleryTO photogallery;

	private int imageCount;
	private int pageCount;
	private int currentPage = 0;

	private PGMultiUpload upload;
	private Div galleryLayout;
	private HorizontalLayout upperPagingLayout;
	private HorizontalLayout lowerPagingLayout;

	/**
	 * Položka z fotogalerie, která byla dle URL vybrána (nepovinné)
	 */
	private Integer pgSelected;
	private String galleryDir;

	private String pageURLBase;

	public PGViewerPage() {
		pageURLBase = getPageURL(photogalleryViewerPageFactory);
		loadCSS(getContextPath() + "/VAADIN/pg/style.css");
	}

	@Override
	public String getPageTitle() {
		return photogallery.getContentNode().getName();
	}

	@Override
	protected void createContentOperations(Div operationsListLayout) {
		super.createContentOperations(operationsListLayout);

		ImageButton downloadZip = new ImageButton("Zabalit do ZIP", ImageIcon.PRESENT_16_ICON,
				event -> new ConfirmDialog("Přejete si vytvořit ZIP galerie?", e -> {
					logger.info("zipPhotogallery thread: {}", Thread.currentThread().getId());
					progressIndicatorWindow = new ProgressDialog();
					eventBus.subscribe(PGViewerPage.this);
					pgService.zipGallery(galleryDir);
				}).open());
		operationsListLayout.add(downloadZip);
	}

	@Override
	public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
		String[] chunks = parameter.split("/");
		String identifierToken = null;
		String pageToken = null;
		String extraToken = null;
		if (chunks.length > 0)
			identifierToken = chunks[0];
		if (chunks.length > 1)
			pageToken = chunks[1];
		if (chunks.length > 2)
			extraToken = chunks[2];

		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
		if (identifier == null)
			throw new GrassPageException(404);

		photogallery = pgService.getPhotogalleryForDetail(identifier.getId());
		if (photogallery == null)
			throw new GrassPageException(404);

		if (!MAGICK_WORD.equals(pageToken) && !MAGICK_WORD.equals(extraToken)
				&& !photogallery.getContentNode().isPublicated() && !isAdminOrAuthor())
			throw new GrassPageException(403);

		galleryDir = photogallery.getPhotogalleryPath();

		if (pageToken != null) {
			try {
				currentPage = Integer.parseInt(pageToken) - 1;
			} catch (NumberFormatException e) {
				// nic, neřešit
			}
		}

		loadJS();
		init();

		UIUtils.turnOffRouterAnchors();
	}

	private boolean isAdminOrAuthor() {
		return getUser().isAdmin() || photogallery.getContentNode().getAuthor().equals(getUser());
	}

	@Override
	protected ContentNodeTO getContentNodeDTO() {
		return photogallery.getContentNode();
	}

	@Override
	protected void createContent(Div layout) {
		// pokud je galerie porušená, pak nic nevypisuj
		try {
			if (!pgService.checkGallery(galleryDir)) {
				layout.add(new Span("Chyba: Galerie je porušená -- kontaktujte administrátora (ID: "
						+ photogallery.getPhotogalleryPath() + ")"));
				return;
			}
		} catch (IllegalStateException e) {
			throw new GrassPageException(500, e);
		} catch (IllegalArgumentException e) {
			throw new GrassPageException(404, e);
		}

		try {
			imageCount = pgService.getViewItemsCount(photogallery.getPhotogalleryPath());
		} catch (Exception e) {
			throw new GrassPageException(500, e);
		}
		pageCount = (int) Math.ceil((double) imageCount / PAGE_SIZE);

		// Horní Layout stránkovacích tlačítek
		upperPagingLayout = new HorizontalLayout();
		upperPagingLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		upperPagingLayout.setSpacing(true);
		upperPagingLayout.setPadding(false);
		layout.add(upperPagingLayout);

		// galerie
		galleryLayout = new Div();
		galleryLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		galleryLayout.setId("gallery-layout");
		galleryLayout.getStyle().set("text-align", "center");
		layout.add(galleryLayout);

		// Spodní layout stránkovacích tlačítek
		lowerPagingLayout = new HorizontalLayout();
		lowerPagingLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		lowerPagingLayout.setSpacing(true);
		lowerPagingLayout.setPadding(false);
		layout.add(lowerPagingLayout);

		upload = new PGMultiUpload(galleryDir) {
			private static final long serialVersionUID = 6886131045258035130L;

			@Override
			protected void allFilesUploaded() {
				eventBus.subscribe(PGViewerPage.this);
				progressIndicatorWindow = new ProgressDialog();
				PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogallery.getContentNode().getName(),
						galleryDir, photogallery.getContentNode().getContentTagsAsStrings(),
						photogallery.getContentNode().isPublicated(), false);
				pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO,
						photogallery.getContentNode().getCreationDate());
			}
		};
		Button uploadButton = new Button("Upload");
		upload.setUploadButton(uploadButton);
		Span dropLabel = new Span("Drop");
		upload.setDropLabel(dropLabel);
		upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		if (coreACL.canModifyContent(photogallery.getContentNode(), getUser()))
			layout.add(upload);

		Div statusRow = new Div();
		statusRow.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
		statusRow.getStyle().set("background", "#fdfaf2").set("padding", "3px 10px").set("line-height", "20px")
				.set("font-size", "12px").set("color", "#777");
		statusRow.setSizeUndefined();
		statusRow.setText("Galerie: " + photogallery.getPhotogalleryPath() + " celkem položek: " + imageCount);
		layout.add(statusRow);

		refreshGrid();

		if (pgSelected != null)
			showItem(pgSelected);
	}

	@Handler
	protected void onProcessStart(final PGProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			progressIndicatorWindow.open();
		});
	}

	@Handler
	protected void onProcessProgress(PGProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();
			UIUtils.redirect(pageURLBase + "/" + URLIdentifierUtils.createURLIdentifier(photogallery.getId(),
					photogallery.getContentNode().getName()) + "/" + (currentPage + 1));
		});
		eventBus.unsubscribe(PGViewerPage.this);
	}

	@Handler
	protected void onProcessStart(final PGZipProcessStartEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			progressIndicatorWindow.setTotal(event.getCountOfStepsToDo());
			progressIndicatorWindow.open();
		});
	}

	@Handler
	protected void onProcessProgress(PGZipProcessProgressEvent event) {
		progressIndicatorWindow.runInUI(() -> progressIndicatorWindow.indicateProgress(event.getStepDescription()));
	}

	@Handler
	protected void onProcessResult(final PGZipProcessResultEvent event) {
		progressIndicatorWindow.runInUI(() -> {
			if (progressIndicatorWindow != null)
				progressIndicatorWindow.close();

			if (event.isSuccess()) {
				WebDialog win = new WebDialog();
				win.addDialogCloseActionListener(e -> pgService.deleteZipFile(event.getZipFile()));
				Anchor link = new Anchor(new StreamResource(photogallery.getPhotogalleryPath() + ".zip", () -> {
					try {
						return Files.newInputStream(event.getZipFile());
					} catch (IOException e1) {
						e1.printStackTrace();
						return null;
					}
				}), "Stáhnout ZIP souboru");
				link.setTarget("_blank");
				win.addComponent(link, Alignment.CENTER);

				Button proceedButton = new Button("Zavřít", e -> win.close());
				win.addComponent(proceedButton, Alignment.CENTER);

				win.open();
			} else {
				UIUtils.showWarning(event.getResultDetails());
			}
		});
		eventBus.unsubscribe(PGViewerPage.this);
	}

	private void refreshGrid() {
		galleryLayout.removeAll();
		if (pageCount == 0) {
			galleryLayout.add(new Text("Galerie neobsahuje žádné položky"));
			return;
		}
		if (currentPage < 0)
			currentPage = 0;
		if (currentPage >= pageCount)
			currentPage = pageCount - 1;
		int start = currentPage * PAGE_SIZE;
		int index = start;
		try {
			for (PhotogalleryViewItemTO item : pgService.getViewItems(galleryDir, start, PAGE_SIZE)) {
				final int currentIndex = index;
				Div itemLayout = new Div();
				itemLayout.getStyle().set("text-align", "center").set("width", "170px").set("display", "inline-block")
						.set("margin-top", "10px");

				// Miniatura/Náhled
				// Název souboru sice nemusí odpovídat, ale měl by pasovat typem -- takže pokud mám SVG a z něj náhled
				// jako PNG soubor, je potřeba předat, že jde už o PNG soubor (nikoliv ten původní SVG) jinak se
				// browser bude snažit číst to PNG jako SVG a nic se nezobrazí
				String fileName = item.getFile().getFileName().toString();

				Image embedded;
				if (fileName.toLowerCase().endsWith(".xcf")) {
					embedded = new Image("img/gimp.png", "XCF file");
					embedded.getElement().getStyle().setMaxHeight("150px");
					embedded.getElement().getStyle().setMaxWidth("150px");
				} else if (fileName.toLowerCase().endsWith(".otf") || fileName.toLowerCase().endsWith(".ttf")) {
					embedded = new Image("img/font.png", "Font file");
				} else {
					embedded = new Image(new StreamResource(fileName, () -> {
						try {
							return Files.newInputStream(item.getFile());
						} catch (IOException e) {
							e.printStackTrace();
							return null;
						}
					}), fileName);
				}
				itemLayout.add(embedded);
				itemLayout.add(new Breakline());

				String str = item.getName();
				if (str.length() > 25)
					str = str.substring(0, 13) + "..." + str.substring(str.length() - 13);
				Span label = new Span(str);
				label.getStyle().set("font-size", "12px");
				itemLayout.add(label);

				itemLayout.add(new Breakline());

				// Detail
				final String urlFinal = PGUtils.createDetailURL(item, photogallery);
				LinkButton detailButton = new LinkButton("Detail", e -> UI.getCurrent().getPage().open(urlFinal));
				itemLayout.add(detailButton);

				// Smazat
				if (coreACL.canModifyContent(photogallery.getContentNode(), getUser())) {
					LinkButton deleteButton = new LinkButton("Smazat", e -> {
						new ConfirmDialog(e2 -> {
							pgService.deleteFile(item, galleryDir);
							eventBus.subscribe(PGViewerPage.this);
							progressIndicatorWindow = new ProgressDialog();
							PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(
									photogallery.getContentNode().getName(), galleryDir,
									photogallery.getContentNode().getContentTagsAsStrings(),
									photogallery.getContentNode().isPublicated(), false);
							pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO,
									photogallery.getContentNode().getCreationDate());
						}).open();
					});
					deleteButton.getStyle().set("margin-left", "20px").set("color", "red");
					itemLayout.add(deleteButton);
				}

				galleryLayout.add(itemLayout);

				embedded.addClickListener(e -> showItem(currentIndex));

				index++;
			}

			populatePaging(upperPagingLayout);
			populatePaging(lowerPagingLayout);
		} catch (Exception e) {
			UIUtils.showWarning("Listování galerie selhalo");
		}
	}

	private void populatePaging(HorizontalLayout layout) {
		layout.removeAll();

		addPagingButton(layout, "<", e -> setPage(currentPage == 0 ? 0 : currentPage - 1), false, false, true);

		HorizontalLayout numberLayout = new HorizontalLayout();
		numberLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		numberLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		numberLayout.setSpacing(true);
		numberLayout.setPadding(false);
		layout.add(numberLayout);
		numberLayout.getElement().getStyle().set("margin-right", "auto").set("margin-left", "auto");

		if (pageCount > 5) {
			addPagingButton(numberLayout, "1", e -> setPage(0), currentPage == 0);
			int pageRadius = Math.min(MAX_PAGE_RADIUS, pageCount / 2 + 1);
			int startPage = Math.max(1, currentPage - pageRadius);
			int endPage = Math.min(currentPage + pageRadius, pageCount - 2);
			if (startPage <= endPage) {
				if (startPage > 1)
					numberLayout.add(new Span("..."));
				for (int i = startPage; i <= endPage; i++) {
					int page = i; // closure!
					addPagingButton(numberLayout, String.valueOf(i + 1), e -> setPage(page), currentPage == page);
				}
				if (endPage < pageCount - 2)
					numberLayout.add(new Span("..."));
				addPagingButton(numberLayout, String.valueOf(pageCount), e -> setPage(pageCount - 1),
						currentPage == pageCount - 1);
			}
		} else {
			for (int i = 1; i <= pageCount; i++) {
				int page = i - 1;
				addPagingButton(numberLayout, String.valueOf(i), e -> setPage(page), currentPage == page);
			}
		}
		addPagingButton(layout, ">", e -> setPage(currentPage == pageCount - 1 ? pageCount - 1 : currentPage + 1),
				false, true, false);
	}

	private void addPagingButton(
			HorizontalLayout layout, String caption,
			ComponentEventListener<ClickEvent<Button>> clickListener, boolean primary) {
		addPagingButton(layout, caption, clickListener, primary, false, false);
	}

	private void addPagingButton(
			HorizontalLayout layout, String caption,
			ComponentEventListener<ClickEvent<Button>> clickListener, boolean primary,
			boolean autoLeftMargin,
			boolean autoRightMargin) {
		Button btn = new Button(caption, clickListener);
		btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
		if (primary)
			btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		btn.getElement().getStyle().set("min-width", "fit-content");
		if (autoLeftMargin)
			btn.getElement().getStyle().set("margin-left", "auto");
		if (autoRightMargin)
			btn.getElement().getStyle().set("margin-right", "auto");
		layout.add(btn);
	}

	private void setPage(int page) {
		if (page == currentPage)
			return;
		currentPage = page;
		refreshGrid();
	}

	private void showItem(final int index) {
		PGSlideshow slideshow = new PGSlideshow(photogallery, imageCount) {
			private static final long serialVersionUID = 7926209313704634472L;

			@Override
			protected void pageUpdate(int currentIndex) {
				// zajisti posuv přehledu
				int newPage = currentIndex / PAGE_SIZE;
				if (newPage != currentPage) {
					currentPage = newPage;
					refreshGrid();
				}
			}

			@Override
			protected PhotogalleryViewItemTO getItem(int index) throws IOException {
				return pgService.getSlideshowItem(galleryDir, index);
			}
		};
		add(slideshow);
		slideshow.showItem(index);
	}

	@Override
	protected PageFactory getContentViewerPageFactory() {
		return photogalleryViewerPageFactory;
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmDialog confirmSubwindow = new ConfirmDialog("Opravdu si přejete smazat tuto galerii ?", ev -> {
			NodeOverviewTO nodeDTO = photogallery.getContentNode().getParent();

			final String nodeURL = getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

			// zdařilo se ? Pokud ano, otevři info okno a při
			// potvrzení jdi na kategorii
			if (pgService.deletePhotogallery(photogallery.getId())) {
				UIUtils.redirect(nodeURL);
			} else {
				// Pokud ne, otevři warn okno a při
				// potvrzení jdi na kategorii
				WarnDialog warnSubwindow = new WarnDialog("Při mazání galerie se nezdařilo smazat některé soubory.");
				warnSubwindow.addDialogCloseActionListener(e -> UIUtils.redirect(nodeURL));
				warnSubwindow.open();
			}
		});
		confirmSubwindow.open();
	}

	@Override
	protected void onEditOperation() {
		UIUtils.redirect(getPageURL(photogalleryEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(photogallery.getId(),
						photogallery.getContentNode().getName())));
	}
}
