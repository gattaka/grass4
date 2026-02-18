package cz.gattserver.grass.print3d.ui.pages;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.stlviewer.STLViewer;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.ui.UploadBuilder;
import cz.gattserver.common.vaadin.dialogs.CopyTagsDialog;
import cz.gattserver.common.vaadin.dialogs.WebDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.print3d.config.Print3dConfiguration;
import cz.gattserver.grass.print3d.interfaces.Print3dPayloadTO;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.Print3dViewItemTO;
import cz.gattserver.grass.print3d.service.Print3dService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@PageTitle("Editor 3D projektu")
@Route(value = "print3d-editor", layout = MainView.class)
public class Print3dEditorPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    @Serial
    private static final long serialVersionUID = -1922351429364968659L;

    private static final Logger logger = LoggerFactory.getLogger(Print3dEditorPage.class);

    private final Print3dService print3dService;
    private final ContentTagService contentTagFacade;
    private final SecurityService securityService;
    private final NodeService nodeService;
    private final EventBus eventBus;

    private NodeOverviewTO node;
    private Print3dTO project;

    private TokenField keywords;
    private TextField nameField;
    private Checkbox publicatedCheckBox;

    private String projectDir;
    private boolean editMode;
    private boolean leaving;

    private final ComponentFactory componentFactory;

    /**
     * Soubory, které byly nahrány od posledního uložení. V případě, že budou úpravy zrušeny, je potřeba tyto soubory
     * smazat.
     */
    private final Set<Print3dViewItemTO> newFiles = new HashSet<>();

    private String operationToken;
    private String identifierToken;

    public Print3dEditorPage(Print3dService print3dService, ContentTagService contentTagFacade,
                             SecurityService securityService, NodeService nodeService, EventBus eventBus) {
        this.securityService = securityService;
        this.nodeService = nodeService;
        this.print3dService = print3dService;
        this.contentTagFacade = contentTagFacade;
        this.eventBus = eventBus;
        this.componentFactory = new ComponentFactory();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (!securityService.getCurrentUser().getRoles().contains(CoreRole.AUTHOR))
            throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

        String[] chunks = parameter.split("/");
        if (chunks.length > 0) operationToken = chunks[0];
        if (chunks.length > 1) identifierToken = chunks[1];

        removeAll();

        Div editorLayout = componentFactory.createOneColumnLayout();
        add(editorLayout);

        UIUtils.addOnbeforeunloadWarning();

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) {
            logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '{}'", identifierToken);
            throw new GrassPageException(404);
        }

        CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback =
                q -> contentTagFacade.findByFilter(q.getFilter(), q.getOffset(), q.getLimit()).stream();
        CallbackDataProvider.CountCallback<String, String> serializableFunction =
                q -> contentTagFacade.countByFilter(q.getFilter());
        keywords = new TokenField(null, fetchItemsCallback, serializableFunction);

        Button copyFromContentButton = componentFactory.createCopyFromContentButton(
                e -> new CopyTagsDialog(list -> list.forEach(keywords::addToken)).open());
        keywords.getChooseElementsDiv().add(copyFromContentButton);

        nameField = new TextField();
        nameField.setValueChangeMode(ValueChangeMode.EAGER);
        publicatedCheckBox = new Checkbox();

        // operace ?
        if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
            editMode = false;
            node = nodeService.getNodeByIdForOverview(identifier.getId());
            nameField.setValue("");
            publicatedCheckBox.setValue(true);
        } else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
            editMode = true;
            project = print3dService.getProjectForDetail(identifier.getId());

            if (project == null) throw new GrassPageException(404);

            nameField.setValue(project.getContentNode().getName());
            for (ContentTagTO tagDTO : project.getContentNode().getContentTags())
                keywords.addToken(tagDTO.getName());

            publicatedCheckBox.setValue(project.getContentNode().isPublicated());

            // nemá oprávnění upravovat tento obsah
            if (!project.getContentNode().getAuthor().getName().equals(securityService.getCurrentUser().getName()) &&
                    !securityService.getCurrentUser().isAdmin()) throw new GrassPageException(403);
        } else {
            logger.debug("Neznámá operace: '{}'", operationToken);
            throw new GrassPageException(404);
        }

        try {
            projectDir = editMode ? project.getPrint3dProjectPath() : print3dService.createProjectDir();
        } catch (IOException e) {
            throw new GrassPageException(500);
        }

        editorLayout.add(new H2("Název projektu"));
        editorLayout.add(nameField);
        nameField.setWidthFull();

        // label
        editorLayout.add(new H2("Klíčová slova"));

        // menu tagů + textfield tagů
        editorLayout.add(keywords);

        keywords.isEnabled();
        keywords.setAllowNewItems(true);
        keywords.getInputField().setPlaceholder("klíčové slovo");

        HorizontalLayout gridLayout = new HorizontalLayout();
        gridLayout.setPadding(false);
        gridLayout.setSpacing(true);
        gridLayout.setWidthFull();
        gridLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(gridLayout);

        final Grid<Print3dViewItemTO> grid = new Grid<>();
        final List<Print3dViewItemTO> items = editMode ? print3dService.getItems(projectDir) : new ArrayList<>();
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setItems(items);

        grid.setWidthFull();
        grid.setHeight("400px");

        grid.addColumn(new TextRenderer<>(p -> p.getName())).setHeader("Název")
                .setFlexGrow(100);

        grid.addColumn(new TextRenderer<>(Print3dViewItemTO::getSize)).setHeader("Velikost").setWidth("80px")
                .setTextAlign(ColumnTextAlign.END).setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(itemTO -> componentFactory.createInlineButton("Zobrazit", e -> {
            // TODO funguje aktuálně pouze pro již nahrané
            String url = UIUtils.getContextPath() + "/" + Print3dConfiguration.PRINT3D_PATH + "/" + projectDir + "/" +
                    itemTO.getPath().getFileName();
            WebDialog previewDialog = new WebDialog("Náhled");
            STLViewer stlViewer = new STLViewer(instance -> instance.show(url));
            stlViewer.setWidth(500, Unit.PIXELS);
            stlViewer.setHeight(500, Unit.PIXELS);
            previewDialog.add(stlViewer);
            previewDialog.add(componentFactory.createDialogStornoLayout(ee -> previewDialog.close()));
            previewDialog.open();
        }))).setHeader("Zobrazit").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(itemTO -> componentFactory.createDeleteInlineButton(e -> {
            try {
                print3dService.deleteFile(itemTO, projectDir);
                items.remove(itemTO);
            } catch (Exception ex) {
                UIUtils.showWarning("Nezdařilo se smazat některé soubory");
            }
            grid.getDataProvider().refreshAll();
        }))).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        gridLayout.add(grid);

        UploadBuilder uploadBuilder = new UploadBuilder();

        // TODO vyřešit práci s přidáváním a odebíráním neuložených souborů atd.
        Upload upload = uploadBuilder.createUpload(set -> {
            for (UploadBuilder.UploadFile uploadFile : set) {
                Print3dViewItemTO itemTO = print3dService.constructViewItemTO(uploadFile.getMetadata().fileName(),
                        uploadFile.getFile().toPath());
                newFiles.add(itemTO);
                items.add(itemTO);
                grid.setItems(items);
            }
        }, () -> {
            Set<String> files = new HashSet<>();
            files.addAll(print3dService.getItems(projectDir).stream().map(Print3dViewItemTO::getName)
                    .collect(Collectors.toSet()));
            files.addAll(newFiles.stream().map(Print3dViewItemTO::getName).collect(Collectors.toSet()));
            return files;
        });
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(upload);

        editorLayout.add(new H2("Nastavení"));

        VerticalLayout chekboxLayout = new VerticalLayout();
        chekboxLayout.setSpacing(true);
        chekboxLayout.setPadding(false);
        editorLayout.add(chekboxLayout);

        publicatedCheckBox.setLabel("Publikovat projekt");
        chekboxLayout.add(publicatedCheckBox);

        Div buttonsLayout = componentFactory.createButtonLayout();
        buttonsLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(buttonsLayout);

        populateButtonsLayout(buttonsLayout);
    }

    private void populateButtonsLayout(Div buttonLayout) {
        // Uložit

        ComponentFactory componentFactory = new ComponentFactory();
        Button saveButton = componentFactory.createSaveButton(event -> {
            if (!isFormValid()) return;
            saveOrUpdateProject();
        });
        buttonLayout.add(saveButton);

        // Uložit a zavřít
        Button saveAndCloseButton = componentFactory.createSaveAndCloseButton(event -> {
            if (!isFormValid()) return;
            leaving = true;
            saveOrUpdateProject();
        });
        buttonLayout.add(saveAndCloseButton);
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);

        buttonLayout.add(componentFactory.createStornoButton(ev -> {
            leaving = true;
            if (editMode) returnToProject();
            else returnToNode();
        }));
    }

    private boolean isFormValid() {
        String name = nameField.getValue();
        if (name == null || name.isEmpty()) {
            UIUtils.showWarning("Název projektu nemůže být prázdný");
            return false;
        }
        return true;
    }

    private void saveOrUpdateProject() {
        for (Print3dViewItemTO item : newFiles) {
            try {
                print3dService.uploadFile(Files.newInputStream(item.getPath()), item.getName(), projectDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Print3dPayloadTO payloadTO = new Print3dPayloadTO(nameField.getValue(), projectDir, keywords.getValue(),
                publicatedCheckBox.getValue());

        eventBus.subscribe(Print3dEditorPage.this);

        if (editMode) {
            print3dService.modifyProject(project.getId(), payloadTO);
            onModifyResult(project.getId());
        } else {
            onSaveResult(print3dService.saveProject(payloadTO, node.getId(), securityService.getCurrentUser().getId()));
        }
    }

    /**
     * Zavolá vrácení se na obsah
     */
    private void returnToProject() {
        UIUtils.removeOnbeforeunloadWarning().then(e -> UI.getCurrent().navigate(Print3DViewerPage.class,
                URLIdentifierUtils.createURLIdentifier(project.getId(), project.getContentNode().getName())));
    }

    /**
     * zavolání vrácení se na kategorii
     */
    private void returnToNode() {
        UIUtils.removeOnbeforeunloadWarning().then(e -> UI.getCurrent()
                .navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName())));
    }

    private void onSaveResult(Long id) {
        if (id != null) {
            project = print3dService.getProjectForDetail(id);
            // soubory byly uloženy a nepodléhají
            // podmíněnému smazání
            newFiles.clear();
            if (leaving) returnToProject();
            // odteď budeme editovat
            editMode = true;
        } else {
            UIUtils.showWarning("Uložení projektu se nezdařilo");
        }
    }

    private void onModifyResult(Long id) {
        if (id != null) {
            // soubory byly uloženy a nepodléhají
            // podmíněnému smazání
            newFiles.clear();
            if (leaving) returnToProject();
        } else {
            UIUtils.showWarning("Úprava projektu se nezdařila");
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