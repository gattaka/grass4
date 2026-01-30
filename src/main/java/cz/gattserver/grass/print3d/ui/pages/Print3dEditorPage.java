package cz.gattserver.grass.print3d.ui.pages;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.DownloadHandler;
import com.vaadin.flow.server.streams.DownloadResponse;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.util.HumanBytesSizeFormatter;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.CopyTagsFromContentChooseDialog;
import cz.gattserver.grass.core.events.EventBus;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.grass.print3d.interfaces.Print3dPayloadTO;
import cz.gattserver.grass.print3d.interfaces.Print3dTO;
import cz.gattserver.grass.print3d.interfaces.Print3dViewItemTO;
import cz.gattserver.grass.print3d.service.Print3dService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("Editor 3D projektu")
@Route(value = "print3d-editor", layout = MainView.class)
public class Print3dEditorPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    private static final long serialVersionUID = 8685208356478891386L;

    private static final Logger logger = LoggerFactory.getLogger(Print3dEditorPage.class);

    private static final String CLOSE_JS_DIV_ID = "close-js-div";

    @Autowired
    private Print3dService print3dService;

    @Autowired
    private ContentTagService contentTagFacade;

    @Resource(name = "print3dViewerPageFactory")
    private PageFactory print3dViewerPageFactory;

    @Autowired
    private EventBus eventBus;

    private NodeOverviewTO node;
    private Print3dTO project;

    private TokenField keywords;
    private TextField nameField;
    private Checkbox publicatedCheckBox;

    private String projectDir;
    private boolean editMode;
    private boolean stayInEditor = false;

    /**
     * Soubory, které byly nahrány od posledního uložení. V případě, že budou úpravy zrušeny, je potřeba tyto soubory
     * smazat.
     */
    private Set<Print3dViewItemTO> newFiles = new HashSet<>();

    private String operationToken;
    private String identifierToken;

    private SecurityService securityService;
    private NodeService nodeService;

    public Print3dEditorPage(SecurityService securityService, NodeService nodeService) {
        this.securityService = securityService;
        this.nodeService = nodeService;
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (!SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles().contains(CoreRole.AUTHOR))
            throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

        String[] chunks = parameter.split("/");
        if (chunks.length > 0) operationToken = chunks[0];
        if (chunks.length > 1) identifierToken = chunks[1];

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div editorLayout = componentFactory.createOneColumnLayout();
        add(editorLayout);

        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = function() { return \"Opravdu si přejete ukončit editor a odejít - " +
                        "rozpracovaná data nejsou uložena ?\" };");

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) {
            logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: '{}'", identifierToken);
            throw new GrassPageException(404);
        }

        CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback =
                q -> contentTagFacade.findByFilter(q.getFilter().get(), q.getOffset(), q.getLimit()).stream();
        CallbackDataProvider.CountCallback<String, String> serializableFunction =
                q -> contentTagFacade.countByFilter(q.getFilter().get());
        keywords = new TokenField(fetchItemsCallback, serializableFunction);

        Button copyFromContentButton = componentFactory.createCopyFromContentButton(
                e -> new CopyTagsFromContentChooseDialog(list -> list.forEach(keywords::addToken)).open());
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
            for (ContentTagOverviewTO tagDTO : project.getContentNode().getContentTags())
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
        final List<Print3dViewItemTO> items;
        if (editMode) {
            try {
                items = print3dService.getItems(projectDir);
            } catch (IOException e) {
                throw new GrassPageException(500, e);
            }
        } else {
            items = new ArrayList<>();
        }
        UIUtils.applyGrassDefaultStyle(grid);
        grid.setItems(items);

        grid.setWidthFull();
        grid.setHeight("400px");

        grid.addColumn(new TextRenderer<>(p -> p.getFile().getFileName().toString())).setHeader("Název")
                .setFlexGrow(100);

        grid.addColumn(new TextRenderer<>(p -> p.getSize())).setHeader("Velikost").setWidth("80px")
                .setTextAlign(ColumnTextAlign.END).setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(itemTO -> {
            String file = itemTO.getFile().getFileName().toString();
            Anchor anchor = new Anchor(DownloadHandler.fromInputStream(e -> {
                try {
                    return new DownloadResponse(Files.newInputStream(print3dService.getFullImage(projectDir, file)),
                            file, null, -1);
                } catch (IOException e1) {
                    UIUtils.showWarning("Obrázek nelze zobrazit");
                    return null;
                }
            }), "Zobrazit");
            anchor.setTarget("_blank");
            return anchor;
        })).setHeader("Zobrazit").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        grid.addColumn(new ComponentRenderer<>(itemTO -> componentFactory.createInlineButton("Smazat", be -> {
            new ConfirmDialog("Opravdu smazat?", e -> {
                try {
                    print3dService.deleteFile(itemTO, projectDir);
                    items.remove(itemTO);
                } catch (Exception ex) {
                    UIUtils.showWarning("Nezdařilo se smazat některé soubory");
                }
                grid.getDataProvider().refreshAll();
            }).open();
        }))).setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setAutoWidth(true);

        gridLayout.add(grid);

        Print3dMultiUpload upload = new Print3dMultiUpload(projectDir) {
            private static final long serialVersionUID = 8317049226635860025L;

            @Override
            protected void fileUploadSuccess(String fileName, long size) {
                Print3dViewItemTO itemTO = new Print3dViewItemTO();
                String sizeText = null;
                sizeText = HumanBytesSizeFormatter.format(size);
                itemTO.setSize(sizeText);
                itemTO.setFile(Paths.get(fileName));
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

        publicatedCheckBox.setLabel("Publikovat projekt");
        chekboxLayout.add(publicatedCheckBox);

        Div buttonsLayout = componentFactory.createButtonLayout();
        buttonsLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        editorLayout.add(buttonsLayout);

        populateButtonsLayout(buttonsLayout);
    }


    private void populateButtonsLayout(Div buttonLayout) {
        Div closeJsDiv = new Div() {
            private static final long serialVersionUID = -7319482130016598549L;

            @ClientCallable
            private void returnToProjectCallback() {
                UI.getCurrent().navigate(Print3dViewerPage.class,
                        URLIdentifierUtils.createURLIdentifier(project.getId(), project.getContentNode().getName()));
            }

            @ClientCallable
            private void returnToNodeCallback() {
                UI.getCurrent()
                        .navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));
            }
        };
        closeJsDiv.setId(CLOSE_JS_DIV_ID);
        add(closeJsDiv);

        // Uložit

        ComponentFactory componentFactory = new ComponentFactory();
        Button saveButton = componentFactory.createSaveButton(event -> {
            if (!isFormValid()) return;
            stayInEditor = true;
            saveOrUpdateProject();
        });
        buttonLayout.add(saveButton);

        // Uložit a zavřít
        Button saveAndCloseButton = componentFactory.createSaveAndCloseButton(event -> {
            if (!isFormValid()) return;
            stayInEditor = false;
            saveOrUpdateProject();
        });
        buttonLayout.add(saveAndCloseButton);
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);

        buttonLayout.add(componentFactory.createStornoButton(ev -> new ConfirmDialog(
                "Opravdu si přejete zavřít editor projektu ? Veškeré neuložené změny budou ztraceny.", e -> {
            cleanAfterCancelEdit();
            if (editMode) returnToProject();
            else returnToNode();
        }).open()));
    }

    private void cleanAfterCancelEdit() {
        if (editMode) {
            print3dService.deleteFiles(newFiles, projectDir);
        } else {
            try {
                print3dService.deleteDraft(projectDir);
            } catch (Exception e) {
                logger.error("Nezdařilo se smazat zrušený rozpracovaný projekt", e);
                throw new GrassPageException(500, e);
            }
        }
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
        Print3dPayloadTO payloadTO = new Print3dPayloadTO(nameField.getValue(), projectDir, keywords.getValues(),
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
        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = null; document.getElementById('" + CLOSE_JS_DIV_ID +
                        "').$server.returnToProjectCallback();");
    }

    /**
     * zavolání vrácení se na kategorii
     */
    private void returnToNode() {
        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = null; document.getElementById('" + CLOSE_JS_DIV_ID +
                        "').$server.returnToNodeCallback();");
    }

    private void onSaveResult(Long id) {
        if (id != null) {
            project = print3dService.getProjectForDetail(id);
            // soubory byly uloženy a nepodléhají
            // podmíněnému smazání
            newFiles.clear();
            if (!stayInEditor) returnToProject();
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
            if (!stayInEditor) returnToProject();
        } else {
            UIUtils.showWarning("Úprava projektu se nezdařila");
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