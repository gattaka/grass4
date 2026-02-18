package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.slideshow.ImageSlideshow;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.ui.UploadBuilder;
import cz.gattserver.common.vaadin.Breakline;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.DownloadDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.UserInfoTO;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.pg.events.impl.*;
import cz.gattserver.grass.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.template.ContentViewer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.pg.util.PGUtils;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Route(value = "photogallery", layout = MainView.class)
public class PGViewerPage extends Div implements HasUrlParameter<String>, HasDynamicTitle {

    @Serial
    private static final long serialVersionUID = 1245583352776185949L;

    private static final Logger logger = LoggerFactory.getLogger(PGViewerPage.class);

    private static final int MAX_PAGE_RADIUS = 2;
    private static final int PAGE_SIZE = 12;

    private static final String MAGICK_WORD = "MAG1CK";

    private final PGService pgService;
    private final EventBus eventBus;
    private final SecurityService securityService;
    private final CoreACLService coreACLService;

    private ProgressDialog progressDialog;

    private PhotogalleryTO photogalleryTO;

    private int imageCount;
    private int pageCount;
    private int currentPage = 0;
    private int startIndex;

    private List<PhotogalleryViewItemTO> currentPageItems;

    private Div galleryLayout;
    private HorizontalLayout upperPagingLayout;
    private HorizontalLayout lowerPagingLayout;

    private String galleryDir;

    private ComponentFactory componentFactory;

    public PGViewerPage(PGService pgService, EventBus eventBus, SecurityService securityService,
                        CoreACLService coreACLService) {
        this.pgService = pgService;
        this.eventBus = eventBus;
        this.securityService = securityService;
        this.coreACLService = coreACLService;
        componentFactory = new ComponentFactory();
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @WildcardParameter String parameter) {
        String[] chunks = parameter.split("/");
        String identifierToken = null;
        String pageToken = null;
        String extraToken = null;
        if (chunks.length > 0) identifierToken = chunks[0];
        if (chunks.length > 1) pageToken = chunks[1];
        if (chunks.length > 2) extraToken = chunks[2];

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) throw new GrassPageException(404);

        UserInfoTO userInfoTO = securityService.getCurrentUser();

        photogalleryTO =
                pgService.findPhotogalleryForDetail(identifier.getId(), userInfoTO.getId(), userInfoTO.isAdmin());
        if (photogalleryTO == null) throw new GrassPageException(404);

        if (!MAGICK_WORD.equals(pageToken) && !MAGICK_WORD.equals(extraToken) && !photogalleryTO.isPublicated() &&
                !isAdminOrAuthor()) throw new GrassPageException(403);

        galleryDir = photogalleryTO.getPhotogalleryPath();

        if (pageToken != null) {
            try {
                currentPage = Integer.parseInt(pageToken) - 1;
            } catch (NumberFormatException e) {
                // nic, neřešit
            }
        }

        removeAll();
        ContentViewer contentViewer = new ContentViewer(createContent(), photogalleryTO, e -> onDeleteOperation(),
                e -> UI.getCurrent()
                        .navigate(PGEditorPage.class, DefaultContentOperations.EDIT.withParameter(parameter)),
                new RouterLink(photogalleryTO.getName(), PGViewerPage.class, parameter));
        add(contentViewer);

        Button downloadZip =
                componentFactory.createZipButton(event -> new ConfirmDialog("Přejete si vytvořit ZIP galerie?", e -> {
                    logger.info("zipPhotogallery thread: {}", Thread.currentThread().threadId());
                    progressDialog = new ProgressDialog();
                    eventBus.subscribe(PGViewerPage.this);
                    pgService.zipGallery(galleryDir);
                }).open());
        contentViewer.getOperationsListLayout().add(downloadZip);

        UIUtils.turnOffRouterAnchors();
    }


    @Override
    public String getPageTitle() {
        return photogalleryTO.getName();
    }


    private boolean isAdminOrAuthor() {
        return securityService.getCurrentUser().isAdmin() ||
                photogalleryTO.getAuthorId().equals(securityService.getCurrentUser().getId());
    }

    protected Div createContent() {
        Div layout = new Div();
        // pokud je galerie porušená, pak nic nevypisuj
        try {
            if (!pgService.checkGallery(galleryDir)) {
                layout.add(new Span("Chyba: Galerie je porušená -- kontaktujte administrátora (ID: " +
                        photogalleryTO.getPhotogalleryPath() + ")"));
                return layout;
            }
        } catch (IllegalStateException e) {
            throw new GrassPageException(500, e);
        } catch (IllegalArgumentException e) {
            throw new GrassPageException(404, e);
        }

        try {
            imageCount = pgService.getViewItemsCount(photogalleryTO.getPhotogalleryPath());
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

        UploadBuilder uploadBuilder = new UploadBuilder();
        Upload upload = uploadBuilder.createUpload(set -> {
            if (set.isEmpty()) return;
            for (UploadBuilder.UploadFile file : set) {
                try {
                    pgService.uploadFile(new FileInputStream(file.getFile()), file.getMetadata().fileName(),
                            galleryDir);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            eventBus.subscribe(PGViewerPage.this);
            progressDialog = new ProgressDialog();
            PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogalleryTO.getName(), galleryDir,
                    photogalleryTO.getContentTags().stream().map(ContentTagTO::getName).toList(),
                    photogalleryTO.isPublicated(), false);
            pgService.modifyPhotogallery(UUID.randomUUID(), photogalleryTO.getId(), payloadTO,
                    photogalleryTO.getCreationDate());
        }, () -> pgService.getItems(galleryDir).stream().map(PhotogalleryViewItemTO::getName)
                .collect(Collectors.toSet()));
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        if (coreACLService.canModifyContent(photogalleryTO, securityService.getCurrentUser())) layout.add(upload);

        Div statusRow = new Div();
        statusRow.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        statusRow.getStyle().set("background", "#fdfaf2").set("padding", "3px 10px").set("line-height", "20px")
                .set("font-size", "12px").set("color", "#777");
        statusRow.setSizeUndefined();
        statusRow.setText("Galerie: " + photogalleryTO.getPhotogalleryPath() + " celkem položek: " + imageCount);
        layout.add(statusRow);

        refreshGrid();

        return layout;
    }

    @Handler
    protected void onProcessStart(final PGProcessStartEvent event) {
        progressDialog.runInUI(() -> {
            progressDialog.setTotal(event.getCountOfStepsToDo());
            progressDialog.open();
        });
    }

    @Handler
    protected void onProcessProgress(PGProcessProgressEvent event) {
        progressDialog.runInUI(() -> progressDialog.indicateProgress(event.getStepDescription()));
    }

    @Handler
    protected void onProcessResult(final PGProcessResultEvent event) {
        progressDialog.runInUI(() -> {
            if (progressDialog != null) progressDialog.close();
            UI.getCurrent().getPage().reload();
        });
        eventBus.unsubscribe(PGViewerPage.this);
    }

    @Handler
    protected void onProcessStart(final PGZipProcessStartEvent event) {
        progressDialog.runInUI(() -> {
            progressDialog.setTotal(event.getCountOfStepsToDo());
            progressDialog.open();
        });
    }

    @Handler
    protected void onProcessProgress(PGZipProcessProgressEvent event) {
        progressDialog.runInUI(() -> progressDialog.indicateProgress(event.getStepDescription()));
    }

    @Handler
    protected void onProcessResult(final PGZipProcessResultEvent event) {
        progressDialog.runInUI(() -> {
            if (progressDialog != null) progressDialog.close();
            if (event.isSuccess()) {
                new DownloadDialog("Komprese", () -> {
                    try {
                        return new DownloadResponse(Files.newInputStream(event.getZipFile()),
                                photogalleryTO.getPhotogalleryPath() + ".zip", null, -1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }, e -> pgService.deleteZipFile(event.getZipFile())).open();
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
        if (currentPage < 0) currentPage = 0;
        if (currentPage >= pageCount) currentPage = pageCount - 1;
        startIndex = currentPage * PAGE_SIZE;
        int index = startIndex;
        currentPageItems = new ArrayList<>();
        try {
            for (PhotogalleryViewItemTO item : pgService.getViewItems(galleryDir, startIndex, PAGE_SIZE)) {
                currentPageItems.add(item);

                final int currentIndex = index;
                Div itemLayout = new Div();
                itemLayout.getStyle().set("text-align", "center").set("width", "170px").set("display", "inline-block")
                        .set("margin-top", "10px");

                // Miniatura/Náhled
                // Název souboru sice nemusí odpovídat, ale měl by pasovat typem -- takže pokud mám SVG a z něj náhled
                // jako PNG soubor, je potřeba předat, že jde už o PNG soubor (nikoliv ten původní SVG) jinak se
                // browser bude snažit číst to PNG jako SVG a nic se nezobrazí
                String fileName = item.getName();

                Image embedded;
                if (fileName.toLowerCase().endsWith(".xcf")) {
                    embedded = new Image("img/gimp.png", "XCF file");
                    embedded.getElement().getStyle().setMaxHeight("150px");
                    embedded.getElement().getStyle().setMaxWidth("150px");
                } else if (fileName.toLowerCase().endsWith(".otf") || fileName.toLowerCase().endsWith(".ttf")) {
                    embedded = new Image("img/font.png", "Font file");
                } else {
                    embedded = new Image(PGUtils.createPhotogalleryBaseURL(photogalleryTO) + item.getMiniaturePath(),
                            fileName);
                }
                itemLayout.add(embedded);
                itemLayout.add(new Breakline());

                String str = item.getName();
                if (str.length() > 20) str = str.substring(0, 13) + "..." + str.substring(str.length() - 13);
                Span label = new Span(str);
                label.getStyle().set("font-size", "12px");
                itemLayout.add(label);

                itemLayout.add(new Breakline());

                Div buttonLayout = new Div();
                buttonLayout.addClassName("pg-thumb-button-div");
                itemLayout.add(buttonLayout);

                // Detail
                final String urlFinal = PGUtils.createPhotogalleryBaseURL(photogalleryTO) + item.getFullPath();
                Div detailButton =
                        componentFactory.createInlineButton("Detail", e -> UI.getCurrent().getPage().open(urlFinal));
                buttonLayout.add(detailButton);

                // Smazat
                if (coreACLService.canModifyContent(photogalleryTO, securityService.getCurrentUser())) {
                    Div deleteButton = componentFactory.createInlineButton("Smazat", e -> new ConfirmDialog(e2 -> {
                        pgService.deleteFile(item.getName(), galleryDir);
                        eventBus.subscribe(PGViewerPage.this);
                        progressDialog = new ProgressDialog();
                        PhotogalleryPayloadTO payloadTO =
                                new PhotogalleryPayloadTO(photogalleryTO.getName(), galleryDir,
                                        photogalleryTO.getContentTags().stream().map(ContentTagTO::getName).toList(),
                                        photogalleryTO.isPublicated(), false);
                        pgService.modifyPhotogallery(UUID.randomUUID(), photogalleryTO.getId(), payloadTO,
                                photogalleryTO.getCreationDate());
                    }).open());
                    deleteButton.getStyle().set("color", "red");
                    buttonLayout.add(deleteButton);
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
                if (startPage > 1) numberLayout.add(new Span("..."));
                for (int i = startPage; i <= endPage; i++) {
                    int page = i; // closure!
                    addPagingButton(numberLayout, String.valueOf(i + 1), e -> setPage(page), currentPage == page);
                }
                if (endPage < pageCount - 2) numberLayout.add(new Span("..."));
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

    private void addPagingButton(HorizontalLayout layout, String caption,
                                 ComponentEventListener<ClickEvent<Button>> clickListener, boolean primary) {
        addPagingButton(layout, caption, clickListener, primary, false, false);
    }

    private void addPagingButton(HorizontalLayout layout, String caption,
                                 ComponentEventListener<ClickEvent<Button>> clickListener, boolean primary,
                                 boolean autoLeftMargin, boolean autoRightMargin) {
        Button btn = new Button(caption, clickListener);
        btn.addThemeVariants(ButtonVariant.LUMO_SMALL);
        if (primary) btn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        btn.getElement().getStyle().set("min-width", "fit-content");
        if (autoLeftMargin) btn.getElement().getStyle().set("margin-left", "auto");
        if (autoRightMargin) btn.getElement().getStyle().set("margin-right", "auto");
        layout.add(btn);
    }

    private void setPage(int page) {
        if (page == currentPage) return;
        currentPage = page;
        refreshGrid();
    }

    private void showItem(final int index) {
        Consumer<Integer> pageUpdateListener = currentIndex -> {
            // zajisti posuv přehledu
            int newPage = currentIndex / PAGE_SIZE;
            if (newPage != currentPage) {
                currentPage = newPage;
                refreshGrid();
            }
        };

        Function<Integer, PhotogalleryViewItemTO> itemByIndexProvider = i -> currentPageItems.get(i - startIndex);

        String photogalleryBasePath = PGUtils.createPhotogalleryBaseURL(photogalleryTO);
        Function<PhotogalleryViewItemTO, String> itemSlideshowURLProvider =
                item -> photogalleryBasePath + item.getSlideshowPath();
        Function<PhotogalleryViewItemTO, String> itemDetailURLProvider =
                item -> photogalleryBasePath + item.getFullPath();

        ImageSlideshow<PhotogalleryViewItemTO> slideshow =
                new ImageSlideshow<>(imageCount, pageUpdateListener, itemByIndexProvider, itemSlideshowURLProvider,
                        itemDetailURLProvider);
        add(slideshow);
        slideshow.showItem(index);
    }

    protected void onDeleteOperation() {
        ConfirmDialog confirmSubwindow = new ConfirmDialog("Opravdu si přejete smazat tuto galerii ?", ev -> {

            String urlIdentifier = URLIdentifierUtils.createURLIdentifier(photogalleryTO.getParentId(),
                    photogalleryTO.getParentName());

            // zdařilo se ? Pokud ano, otevři info okno a při
            // potvrzení jdi na kategorii
            if (pgService.deletePhotogallery(photogalleryTO.getId())) {
                UI.getCurrent().navigate(NodePage.class, urlIdentifier);
            } else {
                // Pokud ne, otevři warn okno a při
                // potvrzení jdi na kategorii
                WarnDialog warnSubwindow = new WarnDialog("Při mazání galerie se nezdařilo smazat některé soubory.");
                warnSubwindow.addDialogCloseActionListener(
                        e -> UI.getCurrent().navigate(NodePage.class, urlIdentifier));
                warnSubwindow.open();
            }
        });
        confirmSubwindow.open();
    }
}
