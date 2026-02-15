package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.ui.UploadBuilder;
import cz.gattserver.common.vaadin.dialogs.CopyTagsDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass.pg.interfaces.PhotogalleryEditorItemTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.util.UIUtils;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@PageTitle("Editor fotogalerie")
@Route(value = "pg-editor", layout = MainView.class)
public class PGEditorPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    @Serial
    private static final long serialVersionUID = 2801021108823739183L;

    private static final Logger logger = LoggerFactory.getLogger(PGEditorPage.class);

    private final PGService pgService;
    private final ContentTagService contentTagFacade;
    private final EventBus eventBus;
    private final NodeService nodeService;
    private final SecurityService securityService;

    private ProgressDialog progressDialog;

    private NodeOverviewTO node;
    private PhotogalleryTO photogallery;

    private TokenField photogalleryKeywords;
    private TextField photogalleryNameField;
    private DatePicker photogalleryDateField;
    private Checkbox publicatedCheckBox;
    private Checkbox reprocessSlideshowAndMiniCheckBox;

    private String galleryDir;
    private boolean editMode;
    private boolean leaving;

    /**
     * Soubory, které byly nahrány od posledního uložení. V případě, že budou úpravy zrušeny, je potřeba tyto soubory
     * smazat.
     */
    private final Set<PhotogalleryEditorItemTO> newFiles = new HashSet<>();

    private String operationToken;
    private String identifierToken;

    private final ComponentFactory componentFactory;

    public PGEditorPage(PGService pgService, ContentTagService contentTagFacade, EventBus eventBus,
                        NodeService nodeService, SecurityService securityService) {
        this.pgService = pgService;
        this.contentTagFacade = contentTagFacade;
        this.eventBus = eventBus;
        this.nodeService = nodeService;
        this.securityService = securityService;
        this.componentFactory = new ComponentFactory();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (!securityService.getCurrentUser().getRoles().contains(CoreRole.AUTHOR))
            throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

        String[] chunks = parameter.split("/");
        if (chunks.length > 0) operationToken = chunks[0];
        if (chunks.length > 1) identifierToken = chunks[1];

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) {
            logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '{}'", identifierToken);
            throw new GrassPageException(404);
        }

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div editorLayout = componentFactory.createOneColumnLayout();
        add(editorLayout);

        CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback =
                q -> contentTagFacade.findByFilter(q.getFilter(), q.getOffset(), q.getLimit()).stream();
        CallbackDataProvider.CountCallback<String, String> serializableFunction =
                q -> contentTagFacade.countByFilter(q.getFilter());
        photogalleryKeywords = new TokenField(null, fetchItemsCallback, serializableFunction);

        Button copyFromContentButton = componentFactory.createCopyFromContentButton(
                e -> new CopyTagsDialog(list -> list.forEach(photogalleryKeywords::addToken)).open());
        photogalleryKeywords.getChooseElementsDiv().add(copyFromContentButton);

        photogalleryNameField = new TextField();
        photogalleryNameField.setValueChangeMode(ValueChangeMode.EAGER);

        photogalleryDateField = componentFactory.createDatePicker("Přepsat datum vytvoření galerie");
        photogalleryDateField.setWidth("250px");
        photogalleryDateField.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        publicatedCheckBox = new Checkbox();
        reprocessSlideshowAndMiniCheckBox = new Checkbox();

        // operace ?
        if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
            editMode = false;
            node = nodeService.getNodeByIdForOverview(identifier.getId());
            photogalleryNameField.setValue("");
            publicatedCheckBox.setValue(true);
        } else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
            editMode = true;
            photogallery = pgService.getPhotogalleryForDetail(identifier.getId());

            if (photogallery == null) throw new GrassPageException(404);

            photogalleryNameField.setValue(photogallery.getContentNode().getName());
            for (ContentTagOverviewTO tagDTO : photogallery.getContentNode().getContentTags())
                photogalleryKeywords.addToken(tagDTO.getName());

            publicatedCheckBox.setValue(photogallery.getContentNode().isPublicated());
            photogalleryDateField.setValue(photogallery.getContentNode().getCreationDate().toLocalDate());

            // nemá oprávnění upravovat tento obsah
            if (!photogallery.getContentNode().getAuthor().getName()
                    .equals(securityService.getCurrentUser().getName()) && !securityService.getCurrentUser().isAdmin())
                throw new GrassPageException(403);
        } else {
            logger.debug("Neznámá operace: '{}'", operationToken);
            throw new GrassPageException(404);
        }

        try {
            galleryDir = editMode ? photogallery.getPhotogalleryPath() : pgService.createGalleryDir();
        } catch (IOException e) {
            throw new GrassPageException(500);
        }

        editorLayout.add(new H2("Název galerie"));
        editorLayout.add(photogalleryNameField);
        photogalleryNameField.setWidthFull();

        // label
        editorLayout.add(new H2("Klíčová slova"));

        // menu tagů + textfield tagů
        editorLayout.add(photogalleryKeywords);

        photogalleryKeywords.isEnabled();
        photogalleryKeywords.setAllowNewItems(true);
        photogalleryKeywords.getInputField().setPlaceholder("klíčové slovo");

        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setPadding(false);
        gridLayout.setSpacing(true);
        gridLayout.setWidthFull();
        gridLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(gridLayout);

        final Grid<PhotogalleryEditorItemTO> grid = new Grid<>(PhotogalleryEditorItemTO.class);
        final List<PhotogalleryEditorItemTO> items;
        if (editMode) {
            items = pgService.getItems(galleryDir).stream()
                    .map(item -> new PhotogalleryEditorItemTO(item.getName(), Path.of(item.getFullPath()))).collect(
                            Collectors.toList());
        } else {
            items = new ArrayList<>();
        }
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setItems(items);
        grid.setColumns("name");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.getColumnByKey("name").setHeader("Název");
        grid.setWidthFull();
        grid.setHeight("400px");

        grid.addColumn(new ComponentRenderer<>(itemTO -> {
            String file = itemTO.getName();
            Anchor anchor = new Anchor(DownloadHandler.fromInputStream(e -> {
                try {
                    return new DownloadResponse(Files.newInputStream(pgService.getFullImage(galleryDir, file)), file,
                            null, -1);
                } catch (IOException e1) {
                    UIUtils.showWarning("Obrázek nelze zobrazit");
                    return null;
                }
            }).inline(), "Zobrazit");
            anchor.setTarget("_blank");
            return anchor;
        })).setHeader("Zobrazit").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        gridLayout.add(grid);

        Div buttonLayout = componentFactory.createButtonLayout(false);
        editorLayout.add(buttonLayout);

        Button deleteBtn = componentFactory.createDeleteGridSetButton(selectedItems -> {
            boolean failures = false;
            for (PhotogalleryEditorItemTO itemTO : selectedItems) {
                try {
                    pgService.deleteFile(itemTO.getName(), galleryDir);
                    items.remove(itemTO);
                } catch (Exception ex) {
                    failures = true;
                }
            }
            if (failures) UIUtils.showWarning("Nezdařilo se smazat některé soubory");
            grid.getDataProvider().refreshAll();
        }, grid);
        deleteBtn.setEnabled(false);
        deleteBtn.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        buttonLayout.add(deleteBtn);

        UploadBuilder uploadBuilder = new UploadBuilder();

        // TODO vyřešit práci s přidáváním a odebíráním neuložených souborů atd.
        Upload upload = uploadBuilder.createUpload(set -> {
            for (UploadBuilder.UploadFile uploadFile : set) {
                PhotogalleryEditorItemTO itemTO = new PhotogalleryEditorItemTO(uploadFile.getMetadata().fileName(),
                        uploadFile.getFile().toPath());
                newFiles.add(itemTO);
                items.add(itemTO);
                grid.setItems(items);
            }
        }, () -> {
            Set<String> files = new HashSet<>();
            files.addAll(pgService.getItems(galleryDir).stream().map(PhotogalleryViewItemTO::getName)
                    .collect(Collectors.toSet()));
            files.addAll(newFiles.stream().map(PhotogalleryEditorItemTO::getName).collect(Collectors.toSet()));
            return files;
        }, "image/*", "video/*", ".xcf", ".ttf", ".otf");
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(upload);

        editorLayout.add(new H2("Nastavení"));

        VerticalLayout chekboxLayout = new VerticalLayout();
        chekboxLayout.setSpacing(true);
        chekboxLayout.setPadding(false);
        editorLayout.add(chekboxLayout);

        publicatedCheckBox.setLabel("Publikovat galerii");
        chekboxLayout.add(publicatedCheckBox);
        reprocessSlideshowAndMiniCheckBox.setLabel("Přegenerovat slideshow a miniatury");
        chekboxLayout.add(reprocessSlideshowAndMiniCheckBox);

        editorLayout.add(photogalleryDateField);

        Div buttonsLayout = componentFactory.createButtonLayout();
        buttonsLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(buttonsLayout);

        populateButtonsLayout(buttonsLayout);

        // odchod mimo Vaadin routing není možné nijak odchytit, jediná možnost je moužít native browser JS
        UIUtils.addOnbeforeunloadWarning();
    }

    private void populateButtonsLayout(Div buttonLayout) {
        ComponentFactory componentFactory = new ComponentFactory();

        // Uložit
        Button saveButton = componentFactory.createSaveButton(event -> {
            if (isFormInvalid()) return;
            saveOrUpdatePhotogallery();
        });
        buttonLayout.add(saveButton);

        // Uložit a zavřít
        Button saveAndCloseButton = componentFactory.createSaveAndCloseButton(event -> {
            if (isFormInvalid()) return;
            leaving = true;
            saveOrUpdatePhotogallery();
        });
        buttonLayout.add(saveAndCloseButton);
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);

        buttonLayout.add(componentFactory.createStornoButton(e -> {
            leaving = true;
            if (editMode) returnToPhotogallery();
            else returnToNode();
        }, true));
    }

    private boolean isFormInvalid() {
        String name = photogalleryNameField.getValue();
        if (name == null || name.isEmpty()) {
            UIUtils.showWarning("Název galerie nemůže být prázdný");
            return true;
        }
        return false;
    }

    private void saveOrUpdatePhotogallery() {
        logger.info("saveOrUpdatePhotogallery thread: " + Thread.currentThread().threadId());

        for (PhotogalleryEditorItemTO item : newFiles) {
            try {
                pgService.uploadFile(Files.newInputStream(item.getPath()), item.getName(), galleryDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        PhotogalleryPayloadTO payloadTO =
                new PhotogalleryPayloadTO(photogalleryNameField.getValue(), galleryDir, photogalleryKeywords.getValue(),
                        publicatedCheckBox.getValue(), reprocessSlideshowAndMiniCheckBox.getValue());

        eventBus.subscribe(PGEditorPage.this);
        progressDialog = new ProgressDialog();

        LocalDateTime ldt =
                photogalleryDateField.getValue() == null ? null : photogalleryDateField.getValue().atStartOfDay();
        if (editMode) {
            pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO, ldt);
        } else {
            pgService.savePhotogallery(UUID.randomUUID(), payloadTO, node.getId(),
                    securityService.getCurrentUser().getId(), ldt);
        }
    }

    /**
     * Zavolá vrácení se na galerii
     */
    private void returnToPhotogallery() {
        UIUtils.removeOnbeforeunloadWarning().then(e -> UI.getCurrent().navigate(PGViewerPage.class,
                URLIdentifierUtils.createURLIdentifier(photogallery.getId(), photogallery.getContentNode().getName())));
    }

    /**
     * zavolání vrácení se na kategorii
     */
    private void returnToNode() {
        UIUtils.removeOnbeforeunloadWarning().then(e -> UI.getCurrent()
                .navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
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
            if (editMode) onModifyResult(event);
            else onSaveResult(event);
        });
        eventBus.unsubscribe(PGEditorPage.this);
    }

    private void onSaveResult(PGProcessResultEvent event) {
        Long id = event.getGalleryId();
        if (event.isSuccess() && id != null) {
            photogallery = pgService.getPhotogalleryForDetail(id);
            // soubory byly uloženy a nepodléhají
            // podmíněnému smazání
            newFiles.clear();
            if (leaving) returnToPhotogallery();
            // odteď budeme editovat
            editMode = true;
        } else {
            UIUtils.showWarning("Uložení galerie se nezdařilo");
        }
    }

    private void onModifyResult(PGProcessResultEvent event) {
        if (event.isSuccess()) {
            // soubory byly uloženy a nepodléhají
            // podmíněnému smazání
            newFiles.clear();
            if (leaving) returnToPhotogallery();
        } else {
            UIUtils.showWarning("Úprava galerie se nezdařila");
        }
    }

    /**
     * Odchod přes Vaadin routing lze odchytit tímhle -- nejde ale o 100% řešení,
     * protože všechny ostatní cesty (tab close, refresh, obecně browser manuální URL navigace) tímhle není možné
     * odchytit, takže jediná možnost -- je tedy potřeba doplnit ještě native browser JS onbeforeunload listenerem
     *
     * @param beforeLeaveEvent before navigation event with event details
     */
    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        if (leaving) return;
        beforeLeaveEvent.postpone();
        componentFactory.createBeforeLeaveConfirmDialog(beforeLeaveEvent).open();
    }
}