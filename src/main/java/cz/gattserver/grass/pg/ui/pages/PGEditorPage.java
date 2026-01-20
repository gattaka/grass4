package cz.gattserver.grass.pg.ui.pages;

import com.vaadin.flow.component.ClientCallable;
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
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.CopyTagsFromContentChooseDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.ui.components.button.DeleteGridButton;
import cz.gattserver.grass.pg.events.impl.PGProcessProgressEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessResultEvent;
import cz.gattserver.grass.pg.events.impl.PGProcessStartEvent;
import cz.gattserver.grass.pg.interfaces.PhotogalleryPayloadTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryViewItemTO;
import cz.gattserver.grass.pg.service.PGService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.components.button.CloseButton;
import cz.gattserver.grass.core.ui.dialogs.ProgressDialog;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.OneColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import net.engio.mbassy.listener.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.*;

@Route("pg-editor")
@PageTitle("Editor fotogalerie")
public class PGEditorPage extends OneColumnPage implements HasUrlParameter<String>, BeforeLeaveObserver {

    private static final long serialVersionUID = 8685208356478891386L;

    private static final Logger logger = LoggerFactory.getLogger(PGEditorPage.class);

    private static final String CLOSE_JS_DIV_ID = "close-js-div";

    @Autowired
    private PGService pgService;

    @Autowired
    private ContentTagService contentTagFacade;

    @Resource(name = "pgViewerPageFactory")
    private PageFactory photogalleryViewerPageFactory;

    @Autowired
    private EventBus eventBus;

    private ProgressDialog progressIndicatorWindow;

    private NodeOverviewTO node;
    private PhotogalleryTO photogallery;

    private TokenField photogalleryKeywords;
    private TextField photogalleryNameField;
    private DatePicker photogalleryDateField;
    private Checkbox publicatedCheckBox;
    private Checkbox reprocessSlideshowAndMiniCheckBox;

    private String galleryDir;
    private boolean editMode;
    private boolean stayInEditor = false;

    /**
     * Soubory, které byly nahrány od posledního uložení. V případě, že budou úpravy zrušeny, je potřeba tyto soubory
     * smazat.
     */
    private Set<PhotogalleryViewItemTO> newFiles = new HashSet<>();

    private String operationToken;
    private String identifierToken;

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String[] chunks = parameter.split("/");
        if (chunks.length > 0) operationToken = chunks[0];
        if (chunks.length > 1) identifierToken = chunks[1];

        init();

        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - " +
                        "rozpracovaná data nejsou uložena ?\" };");
    }

    @Override
    protected void createColumnContent(Div editorLayout) {
        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) {
            logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '{}'", identifierToken);
            throw new GrassPageException(404);
        }

        CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback =
                q -> contentTagFacade.findByFilter(q.getFilter().get(), q.getOffset(), q.getLimit()).stream();
        CallbackDataProvider.CountCallback<String, String> serializableFunction =
                q -> contentTagFacade.countByFilter(q.getFilter().get());
        photogalleryKeywords = new TokenField(fetchItemsCallback, serializableFunction);

        Button copyFromContentButton = new Button("Kopírovat z obsahu");
        copyFromContentButton.setIcon(VaadinIcon.COPY.create());
        copyFromContentButton.addClickListener(
                e -> new CopyTagsFromContentChooseDialog(list -> list.forEach(photogalleryKeywords::addToken)).open());
        photogalleryKeywords.getChildren().findFirst().ifPresent(c -> ((Div) c).add(copyFromContentButton));

        photogalleryNameField = new TextField();
        photogalleryNameField.setValueChangeMode(ValueChangeMode.EAGER);

        ComponentFactory componentFactory = new ComponentFactory();

        photogalleryDateField = componentFactory.createDatePicker("Přepsat datum vytvoření galerie");
        photogalleryDateField.setWidth("250px");
        photogalleryDateField.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        publicatedCheckBox = new Checkbox();
        reprocessSlideshowAndMiniCheckBox = new Checkbox();

        // operace ?
        if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
            editMode = false;
            node = nodeFacade.getNodeByIdForOverview(identifier.getId());
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
            if (!photogallery.getContentNode().getAuthor().getName().equals(getUser().getName()) &&
                    !getUser().isAdmin()) throw new GrassPageException(403);
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
        photogalleryKeywords.addClassName(UIUtils.TOP_PULL_CSS_CLASS);
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

        final Grid<PhotogalleryViewItemTO> grid = new Grid<>(PhotogalleryViewItemTO.class);
        final List<PhotogalleryViewItemTO> items;
        if (editMode) {
            try {
                items = pgService.getItems(galleryDir);
            } catch (IOException e) {
                throw new GrassPageException(500, e);
            }
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

        ButtonLayout buttonLayout = new ButtonLayout();
        editorLayout.add(buttonLayout);

        DeleteGridButton<PhotogalleryViewItemTO> deleteBtn = new DeleteGridButton<>("Smazat", selectedItems -> {
            boolean failures = false;
            for (PhotogalleryViewItemTO itemTO : selectedItems) {
                try {
                    pgService.deleteFile(itemTO, galleryDir);
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

        PGMultiUpload upload = new PGMultiUpload(galleryDir) {
            private static final long serialVersionUID = 8317049226635860025L;

            @Override
            protected void fileUploadSuccess(String fileName) {
                PhotogalleryViewItemTO itemTO = new PhotogalleryViewItemTO();
                itemTO.setName(fileName);
                newFiles.add(itemTO);
                items.add(itemTO);
                grid.setItems(items);
            }
        };
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

        ButtonLayout buttonsLayout = new ButtonLayout();
        buttonsLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(buttonsLayout);

        populateButtonsLayout(buttonsLayout);
    }

    private void populateButtonsLayout(ButtonLayout buttonLayout) {
        // Uložit
        Button saveButton = componentFactory.createSaveButton(event -> {
            if (!isFormValid()) return;
            stayInEditor = true;
            saveOrUpdatePhotogallery();
        });
        buttonLayout.add(saveButton);

        // Uložit a zavřít
        Button saveAndCloseButton = componentFactory.createSaveAndCloseButton(event -> {
            if (!isFormValid()) return;
            stayInEditor = false;
            saveOrUpdatePhotogallery();
        });
        buttonLayout.add(saveAndCloseButton);
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);

        // Storno
        CloseButton cancelButton = new CloseButton("Storno", ev -> new ConfirmDialog(
                "Opravdu si přejete zavřít editor galerie ? Veškeré neuložené změny budou ztraceny.", e -> {
            cleanAfterCancelEdit();
            if (editMode) returnToPhotogallery();
            else returnToNode();
        }).open());
        buttonLayout.add(cancelButton);
    }

    private void cleanAfterCancelEdit() {
        if (editMode) {
            pgService.deleteFiles(newFiles, galleryDir);
        } else {
            try {
                pgService.deleteDraftGallery(galleryDir);
            } catch (Exception e) {
                logger.error("Nezdařilo se smazat zrušenou rozpracovanou galerii", e);
                throw new GrassPageException(500, e);
            }
        }
    }

    private boolean isFormValid() {
        String name = photogalleryNameField.getValue();
        if (name == null || name.isEmpty()) {
            UIUtils.showWarning("Název galerie nemůže být prázdný");
            return false;
        }
        return true;
    }

    private void saveOrUpdatePhotogallery() {
        logger.info("saveOrUpdatePhotogallery thread: " + Thread.currentThread().threadId());
        PhotogalleryPayloadTO payloadTO = new PhotogalleryPayloadTO(photogalleryNameField.getValue(), galleryDir,
                photogalleryKeywords.getValues(), publicatedCheckBox.getValue(),
                reprocessSlideshowAndMiniCheckBox.getValue());

        eventBus.subscribe(PGEditorPage.this);
        progressIndicatorWindow = new ProgressDialog();

        LocalDateTime ldt =
                photogalleryDateField.getValue() == null ? null : photogalleryDateField.getValue().atStartOfDay();
        if (editMode) {
            pgService.modifyPhotogallery(UUID.randomUUID(), photogallery.getId(), payloadTO, ldt);
        } else {
            pgService.savePhotogallery(UUID.randomUUID(), payloadTO, node.getId(), getUser().getId(), ldt);
        }
    }

    /**
     * Zavolá vrácení se na galerii
     */
    private void returnToPhotogallery() {
        Div closeJsDiv = new Div() {
            private static final long serialVersionUID = -7319482130016598549L;

            @ClientCallable
            private void closeCallback() {
                UIUtils.redirect(getPageURL(photogalleryViewerPageFactory,
                        URLIdentifierUtils.createURLIdentifier(photogallery.getId(),
                                photogallery.getContentNode().getName())));
            }
        };
        closeJsDiv.setId(CLOSE_JS_DIV_ID);
        add(closeJsDiv);

        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = null; document.getElementById('" + CLOSE_JS_DIV_ID +
                        "').$server.closeCallback();");
    }

    /**
     * zavolání vrácení se na kategorii
     */
    private void returnToNode() {
        Div closeJsDiv = new Div() {
            private static final long serialVersionUID = -7319482130016598549L;

            @ClientCallable
            private void closeCallback() {
                UIUtils.redirect(getPageURL(nodePageFactory,
                        URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
            }
        };
        closeJsDiv.setId(CLOSE_JS_DIV_ID);
        add(closeJsDiv);

        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = null; document.getElementById('" + CLOSE_JS_DIV_ID +
                        "').$server.closeCallback();");
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
            if (progressIndicatorWindow != null) progressIndicatorWindow.close();
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
            if (!stayInEditor) returnToPhotogallery();
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
            if (!stayInEditor) returnToPhotogallery();
        } else {
            UIUtils.showWarning("Úprava galerie se nezdařila");
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent beforeLeaveEvent) {
        beforeLeaveEvent.postpone();
        new ConfirmDialog("Opravdu si přejete ukončit editor a odejít? Rozpracovaná data nebudou uložena.", e -> {
            beforeLeaveEvent.getContinueNavigationAction().proceed();
        }).open();
    }
}