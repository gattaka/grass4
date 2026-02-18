package cz.gattserver.grass.articles.ui.pages;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.CallbackDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.streams.UploadHandler;
import com.vaadin.flow.shared.Registration;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.CopyTagsDialog;
import cz.gattserver.grass.articles.AttachmentsOperationResult;
import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import cz.gattserver.grass.articles.editor.parser.interfaces.*;
import cz.gattserver.grass.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.articles.ui.dialogs.DraftMenuDialog;
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
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlSpan;
import org.apache.commons.lang3.StringUtils;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serial;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@PageTitle("Editor článku")
@Route(value = "articles-editor", layout = MainView.class)
public class ArticlesEditorPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    @Serial
    private static final long serialVersionUID = 7025393827351970836L;

    private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);

    private final NodeService nodeService;
    private final ArticleService articleService;
    private final ContentTagService contentTagService;
    private final PluginRegisterService pluginRegister;
    private final SecurityService securityService;

    // V případě, že jde o úpravu již existujícího článku (ne existujícího draftu)
    private ArticleEditorTO articleEditorTO;

    private TextArea articleTextArea;
    private TokenField articleKeywords;
    private TextField articleNameField;
    private Checkbox publicatedCheckBox;
    private Grid<AttachmentTO> attachmentsGrid;

    private Span autosaveLabel;

    private Registration articleTextAreaFocusRegistration;

    private String operationToken;
    private String identifierToken;

    private final ComponentFactory componentFactory;

    private boolean leaving;

    public ArticlesEditorPage(ArticleService articleService, PluginRegisterService pluginRegisterService,
                              SecurityService securityService, NodeService nodeService,
                              ContentTagService contentTagService) {
        this.articleService = articleService;
        this.pluginRegister = pluginRegisterService;
        this.securityService = securityService;
        this.nodeService = nodeService;
        this.contentTagService = contentTagService;

        componentFactory = new ComponentFactory();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (!securityService.getCurrentUser().getRoles().contains(CoreRole.AUTHOR))
            throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

        String[] chunks = parameter.split("/");
        if (chunks.length > 0) operationToken = chunks[0];
        if (chunks.length > 1) identifierToken = chunks[1];

        removeAll();

        // Vždy nový, postupně je naplněn
        articleEditorTO = new ArticleEditorTO(UIUtils.getContextPath());

        loadArticle();

        // odchod mimo Vaadin routing není možné nijak odchytit, jediná možnost je moužít native browser JS
        UIUtils.addOnbeforeunloadWarning();
    }

    private void loadArticle() {
        List<ArticleDraftOverviewTO> drafts = articleService.getDraftsForUser(securityService.getCurrentUser().getId());
        if (drafts.isEmpty()) {
            // nejsou-li v DB žádné pro přihlášeného uživatele viditelné drafty
            // článků, otevři editor dle operace (new/edit)
            defaultCreateContent();
        } else {
            // pokud jsou nalezeny drafty k dokončení, nabídni je k výběru
            new DraftMenuDialog(drafts, to -> {
                if (to != null) {
                    populateByExistingDraft(to);
                } else {
                    defaultCreateContent();
                }
            }, to -> articleService.deleteArticle(to.getId())).open();
        }
    }

    private void defaultCreateContent() {
        if (operationToken == null || identifierToken == null) {
            logger.debug("Chybí operace nebo identifikátor cíle");
            throw new GrassPageException(404);
        }

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) {
            logger.debug("Nezdařilo se vytěžit URL identifikátor z řetězce: {}", identifierToken);
            throw new GrassPageException(404);
        }

        // operace ?
        if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
            NodeOverviewTO node = nodeService.getNodeByIdForOverview(identifier.getId());
            if (node == null) {
                logger.debug("Neexistující kategorie: {}", identifier.getId());
                throw new GrassPageException(404);
            }
            articleEditorTO.setNodeId(node.getId());
            articleEditorTO.setNodeName(node.getName());
            articleEditorTO.setDraftName("");
            articleEditorTO.setDraftText("");
            articleEditorTO.setDraftPublicated(true);

            // rovnou ulož, ať máme draft id
            articleEditorTO.setDraftId(articleService.saveDraft(articleEditorTO, false));
        } else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
            ArticleTO existingArticle = articleService.getArticleForDetail(identifier.getId());
            // má oprávnění upravovat tento článek?
            if (!existingArticle.getContentNode().getAuthor().equals(securityService.getCurrentUser()) &&
                    !securityService.getCurrentUser().isAdmin()) throw new GrassPageException(403);
            NodeOverviewTO node = existingArticle.getContentNode().getParent();
            articleEditorTO.setNodeId(node.getId());
            articleEditorTO.setNodeName(node.getName());
            articleEditorTO.setExistingArticleId(existingArticle.getId());
            articleEditorTO.setDraftName(existingArticle.getContentNode().getName());
            articleEditorTO.setDraftText(existingArticle.getText());
            articleEditorTO.setDraftPublicated(existingArticle.getContentNode().isPublicated());
            for (ContentTagTO tagDTO : existingArticle.getContentNode().getContentTags())
                articleEditorTO.getDraftTags().add(tagDTO.getName());
            articleEditorTO.getDraftAttachments().addAll(articleService.findAttachments(existingArticle.getId()));
        } else {
            logger.debug("Neznámá operace: {}", operationToken);
            throw new GrassPageException(404);
        }

        createFields();
    }

    private void populateByExistingDraft(ArticleDraftOverviewTO draft) {
        articleEditorTO.setDraftId(draft.getId());
        NodeOverviewTO node = draft.getContentNode().getParent();
        articleEditorTO.setNodeId(node.getId());
        articleEditorTO.setNodeName(node.getName());
        articleEditorTO.setDraftName(draft.getContentNode().getName());
        articleEditorTO.setDraftText(draft.getText());
        articleEditorTO.setDraftPublicated(draft.getContentNode().isPublicated());
        for (ContentTagTO tagDTO : draft.getContentNode().getContentTags())
            articleEditorTO.getDraftTags().add(tagDTO.getName());

        // jedná se o draft již existujícího obsahu?
        if (draft.getContentNode().getDraftSourceId() != null) {
            ArticleTO article = articleService.getArticleForDetail(draft.getContentNode().getDraftSourceId());
            articleEditorTO.setExistingArticleId(article.getId());
        }

        articleEditorTO.getDraftAttachments()
                .addAll(articleService.findAttachments(articleEditorTO.getExistingArticleId()));
        for (AttachmentTO attachmentTO : articleService.findAttachments(draft.getId())) {
            attachmentTO.setDraft(true);
            articleEditorTO.getDraftAttachments().add(attachmentTO);
        }

        createFields();
    }

    private void createFields() {
        Div leftContentLayout = componentFactory.createLeftColumnLayout();
        createLeftColumnContent(leftContentLayout);
        add(leftContentLayout);

        Div rightContentLayout = componentFactory.createRightColumnLayout();
        createRightColumnContent(rightContentLayout);
        add(rightContentLayout);

        populateFields();
    }

    private void createLeftColumnContent(Div layout) {
        List<String> families = new ArrayList<>(pluginRegister.getRegisteredFamilies());
        families.sort((o1, o2) -> {
            if (o1 == null) {
                return o2 == null ? 0 : "".compareTo(o2);
            } else {
                // druhý ber jako prázdný
                return o1.compareTo(Objects.requireNonNullElse(o2, "")); // ani jeden není null
            }
        });

        // Projdi zaregistrované pluginy a vytvoř menu nástrojů
        for (String family : families) {
            Div familyToolsLayout = componentFactory.createButtonLayout();
            Span headerSpan = new Span();
            headerSpan.add(family);
            String desc = pluginRegister.getFamilyDescription(family);
            if (StringUtils.isNotBlank(desc)) headerSpan.add(new HtmlSpan(" " + desc));
            H3 header = new H3(headerSpan);
            layout.add(header);
            layout.add(familyToolsLayout);

            List<EditorButtonResourcesTO> resourcesBundles =
                    new ArrayList<>(pluginRegister.getTagResourcesByFamily(family));
            Collections.sort(resourcesBundles);

            for (EditorButtonResourcesTO resourceBundle : resourcesBundles) {
                String js = createTextareaGetJS() + createTextareaGetSelectionJS() + "$0.$server.handleSelection(\"" +
                        resourceBundle.getPrefix() + "\", \"" + resourceBundle.getSuffix() + "\", start, finish)";
                ComponentEventListener<ClickEvent<Button>> clickListener =
                        event -> UI.getCurrent().getPage().executeJs(js, getElement());

                Button btn;
                if (resourceBundle.getImagePath() != null) {
                    btn = new Button(resourceBundle.getDescription(),
                            new Image(resourceBundle.getImagePath(), resourceBundle.getDescription()), clickListener);
                    btn.setTooltipText(resourceBundle.getTag());
                } else {
                    btn = new Button(resourceBundle.getDescription(), clickListener);
                    btn.getElement().setProperty("title", resourceBundle.getTag());
                }
                familyToolsLayout.add(btn);
            }
        }

        layout.getStyle().set("width", "420px").set("margin-left", "-200px");
    }

    private void createRightColumnContent(Div layout) {
        articleNameField = new TextField();
        articleNameField.setValueChangeMode(ValueChangeMode.EAGER);

        CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback =
                q -> contentTagService.findByFilter(q.getFilter(), q.getOffset(), q.getLimit()).stream();
        CallbackDataProvider.CountCallback<String, String> serializableFunction =
                q -> contentTagService.countByFilter(q.getFilter());
        articleKeywords = new TokenField(null, fetchItemsCallback, serializableFunction);
        articleKeywords.isEnabled();
        articleKeywords.setPlaceholder("klíčové slovo");

        articleTextArea = new TextArea();
        articleTextArea.setHeight("30em");
        articleTextArea.setWidthFull();
        articleTextArea.setValueChangeMode(ValueChangeMode.EAGER);
        publicatedCheckBox = new Checkbox();

        // zavádění listener pro JS listener akcí jako je vepsání tabulátoru
        articleTextAreaFocusRegistration = articleTextArea.addFocusListener(event -> {
            String js = createTextareaGetJS() + "ta.addEventListener('keydown', function(e) {"
                    /*				*/ + "let keyCode = e.keyCode || e.which;"
                    /*				*/ + "if (keyCode == 9) {"
                    /*					*/ + "e.preventDefault();"
                    /*					*/ + createTextareaGetSelectionJS()
                    /*					*/ + "$0.$server.handleTab(start, finish, ta.value);"
                    /*				*/ + "}"
                    /*			*/ + "}, false);";
            UI.getCurrent().getPage().executeJs(js, getElement());
            // je potřeba jenom jednou pro registraci
            articleTextAreaFocusRegistration.remove();
        });
        // aby se zaregistroval JS listener
        articleTextArea.focus();

        layout.add(new H3("Název článku"));
        layout.add(articleNameField);
        articleNameField.setWidthFull();

        // label
        layout.add(new H3("Klíčová slova"));

        // menu tagů + textfield tagů
        layout.add(articleKeywords);

        Button copyFromContentButton = componentFactory.createCopyFromContentButton(
                e -> new CopyTagsDialog(list -> list.forEach(articleKeywords::addToken)).open());
        articleKeywords.getChooseElementsDiv().add(copyFromContentButton);

        layout.add(new H3("Obsah článku"));
        layout.add(articleTextArea);

        layout.add(new H3("Přílohy článku"));
        createAttachmentsGrid(layout);

        layout.add(new H3("Nastavení článku"));
        publicatedCheckBox.setLabel("Publikovat článek");
        layout.add(publicatedCheckBox);

        Div buttonLayout = componentFactory.createButtonLayout();
        layout.add(buttonLayout);

        // Náhled
        Button previewButton = createPreviewButton();
        buttonLayout.add(previewButton);

        // Uložit
        Button saveButton = createSaveButton();
        buttonLayout.add(saveButton);

        // Zrušit
        Button cancelButton = createCancelButton();
        buttonLayout.add(cancelButton);

        // Auto-ukládání
        Span autosaveLabel = createAutosaveLabel();
        buttonLayout.add(autosaveLabel);
    }

    private void createAttachmentsGrid(Div layout) {
        attachmentsGrid = new Grid<>();
        attachmentsGrid.setColumnReorderingAllowed(true);
        attachmentsGrid.setSelectionMode(SelectionMode.NONE);
        UIUtils.applyGrassDefaultStyle(attachmentsGrid);
        attachmentsGrid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(attachmentsGrid);

        attachmentsGrid.setHeight("200px");

        attachmentsGrid.addColumn(AttachmentTO::getName).setHeader("Název").setFlexGrow(100).setSortProperty("name");
        attachmentsGrid.addColumn(AttachmentTO::getSize).setHeader("Velikost").setTextAlign(ColumnTextAlign.END)
                .setWidth("90px").setFlexGrow(0).setSortable(false);
        attachmentsGrid.addColumn(new ComponentRenderer<>(
                        to -> componentFactory.createInlineButton("Stáhnout", e -> handleDownloadAction(to))))
                .setHeader("Stažení").setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);
        attachmentsGrid.addColumn(new ComponentRenderer<>(
                        to -> componentFactory.createInlineButton("Vložit", e -> handleInsertAction(to)))).setHeader("Vložit")
                .setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);
        attachmentsGrid.addColumn(
                        new ComponentRenderer<>(to -> componentFactory.createDeleteInlineButton(e -> handleDeleteAction(to))))
                .setHeader("Smazat").setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);
        attachmentsGrid.addColumn(new LocalDateTimeRenderer<>(AttachmentTO::getLastModified, "d. M. yyyy HH:mm"))
                .setHeader("Upraveno").setAutoWidth(true).setTextAlign(ColumnTextAlign.END)
                .setSortProperty("lastModified");

        attachmentsGrid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) handleInsertAction(e.getItem());
        });

        Upload upload = getUpload();
        layout.add(upload);
    }

    private @NonNull Upload getUpload() {
        Upload upload = new Upload(UploadHandler.toTempFile((metadata, file) -> {
            // vždy ukládá do draft adresáře; při ostrém uložení se přesune
            AttachmentsOperationResult result = articleService.saveDraftAttachment(articleEditorTO.getDraftId(),
                    articleEditorTO.getExistingArticleId(), new FileInputStream(file), metadata.fileName());
            switch (result.getState()) {
                case SUCCESS:
                    AttachmentTO attachmentTO = result.getAttachment();
                    articleEditorTO.getDraftAttachments().add(attachmentTO);
                    populateGrid();
                    break;
                case ALREADY_EXISTS:
                    UIUtils.showWarning("Soubor '" + metadata.fileName() +
                            "' nebylo možné uložit - soubor s tímto názvem již existuje.");
                    break;
                default:
                    UIUtils.showWarning(
                            "Soubor '" + metadata.fileName() + "' nebylo možné uložit - došlo k systémové chybě.");
            }
        }));
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        return upload;
    }

    private Button createPreviewButton() {
        return componentFactory.createPreviewButton(event -> {
            try {
                saveDraft(true);
                String url = RouteConfiguration.forSessionScope().getUrl(ArticlesViewer.class,
                        URLIdentifierUtils.createURLIdentifier(articleEditorTO.getDraftId(),
                                articleEditorTO.getDraftName()));
                UI.getCurrent().getPage().open(url, "_blank");
            } catch (Exception e) {
                logger.error("Při ukládání náhledu článku došlo k chybě", e);
            }
        });
    }

    private Button createSaveButton() {
        Button saveAndCloseButton = componentFactory.createSaveButton(event -> {
            if (isFormInvalid()) return;
            saveArticle();
            leaving = true;
            returnToArticle();
        });
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);
        return saveAndCloseButton;
    }

    private void deleteDraft() {
        articleService.deleteArticle(articleEditorTO.getDraftId());
    }

    private Button createCancelButton() {
        return componentFactory.createStornoButton(event -> new ConfirmDialog(
                "Opravdu si přejete zavřít editor článku? Veškeré neuložené změny budou ztraceny.", e -> {
            deleteDraft();
            // ruším úpravu existujícího článku (vracím se na
            // článek), nebo nového (vracím se do kategorie) ?
            leaving = true;
            if (articleEditorTO.getExistingArticleId() == null) {
                returnToNode();
            } else {
                returnToArticle();
            }
        }).open());
    }

    private void populateFields() {
        articleNameField.setValue(articleEditorTO.getDraftName());
        articleTextArea.setValue(articleEditorTO.getDraftText());
        publicatedCheckBox.setValue(articleEditorTO.isDraftPublicated());
        articleKeywords.addTokens(articleEditorTO.getDraftTags());
        populateGrid();
    }

    private String createTextareaGetJS() {
        return "let ta = $(\"vaadin-text-area\")[0].children[2];";
    }

    private String createTextareaGetSelectionJS() {
        return "let start = ta.selectionStart;" + "let finish = ta.selectionEnd;";
    }

    private void gatherFields() {
        articleEditorTO.setDraftName(articleNameField.getValue());
        articleEditorTO.setDraftText(articleTextArea.getValue());
        articleEditorTO.setDraftTags(articleKeywords.getValue());
        articleEditorTO.setDraftPublicated(publicatedCheckBox.getValue());
    }

    private void saveDraft(boolean asPreview) {
        gatherFields();
        articleEditorTO.setDraftId(articleService.saveDraft(articleEditorTO, asPreview));
    }

    private void saveArticle() {
        gatherFields();
        articleService.saveArticle(articleEditorTO);
    }

    @ClientCallable
    private void handleSelection(String prefix, String suffix, int start, int end) {
        String origtext = articleTextArea.getValue();
        String text = origtext.substring(0, start) + prefix;
        int pos = text.length();
        text = text + origtext.substring(start, end) + suffix;
        if (end < origtext.length()) text += origtext.substring(end);
        articleTextArea.setValue(text);
        focusOnPosition(pos, pos);
    }

    @ClientCallable
    private void handleTab(int start, int finish, String origtext) {
        String preText = origtext.substring(0, start);
        String tabbedText = origtext.substring(start, finish);
        String postText = origtext.substring(finish);
        StringBuilder result = new StringBuilder(preText + '\t');
        int finishShift = 1;
        for (int i = 0; i < tabbedText.length(); i++) {
            char c = tabbedText.charAt(i);
            result.append(c);
            if (c == '\n' && i != tabbedText.length() - 1) {
                result.append('\t');
                finishShift++;
            }
        }
        result.append(postText);
        articleTextArea.setValue(result.toString());
        focusOnPosition(start + 1, finish + finishShift);
    }

    private void focusOnPosition(int start, int finish) {
        articleTextArea.focus();
        UI.getCurrent().getPage()
                .executeJs(createTextareaGetJS() + "$(ta).get(0).setSelectionRange(" + start + "," + finish + ");");
    }

    @ClientCallable
    private void autosaveCallback() {
        try {
            saveDraft(false);
            autosaveLabel.setText(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " Automaticky uloženo");
            autosaveLabel.setClassName("label-ok");
        } catch (Exception e) {
            autosaveLabel.setText(
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " Chyba uložení");
            autosaveLabel.setClassName("label-err");
        }
    }

    private Span createAutosaveLabel() {
        autosaveLabel = new Span();
        int backupTimeout = articleService.getBackupTimeout() * 1000;
        UI.getCurrent().getPage().executeJs("window.autosaveInterval = setInterval(function(){"
                /*		*/ + "let callbackDiv = $0;"
                /*		*/ + "if (callbackDiv) {"
                /*			*/ + "callbackDiv.$server.autosaveCallback()"
                /*		*/ + "} else {"
                /*			*/ + "clearInterval(window.autosaveInterval); "
                /*		*/ + "}"
                /*		*/ + "}, " + backupTimeout + ");", getElement());

        return autosaveLabel;
    }

    private void populateGrid() {
        attachmentsGrid.setItems(articleEditorTO.getDraftAttachments());
    }

    private String getDownloadLink(AttachmentTO item) {
        String id =
                String.valueOf(item.isDraft() ? articleEditorTO.getDraftId() : articleEditorTO.getExistingArticleId());
        return String.join("/", UIUtils.getURLBase(), ArticlesConfiguration.ATTACHMENTS_PATH, id, item.getName());
    }

    private void handleDownloadAction(AttachmentTO item) {
        UI.getCurrent().getPage().open(getDownloadLink(item));
    }

    // Poznámka:
    // Aktuálně není řešeno, že pokud se draft zavře a obnoví,
    // neobnoví se info o plánovaných smazání příloh existujícího článku
    private void handleDeleteAction(AttachmentTO to) {
        articleEditorTO.getDraftAttachments().remove(to);
        // reálné mazání proveď pouze, pokud jde o draft přílohy
        // ostré přílohy se mažou až při ostrém uložení článku
        if (to.isDraft()) {
            try {
                Files.delete(to.getPath());
            } catch (IOException e) {
                throw new RuntimeException("Nezdařilo se smazat soubor přílohy", e);
            }
        }
        populateGrid();
    }

    private void handleInsertAction(AttachmentTO to) {
        String url = getDownloadLink(to);
        String js = createTextareaGetJS() + createTextareaGetSelectionJS() + "$0.$server.handleSelection(\"" + url +
                "\", \"\", start, finish)";
        UI.getCurrent().getPage().executeJs(js, getElement());
    }

    private boolean isFormInvalid() {
        String name = articleNameField.getValue();
        if (name == null || name.isEmpty()) {
            UIUtils.showWarning("Název článku nemůže být prázdný");
            return true;
        }
        return false;
    }

    @ClientCallable
    private void returnToNodeCallback() {
        UI.getCurrent().navigate(NodePage.class,
                URLIdentifierUtils.createURLIdentifier(articleEditorTO.getNodeId(), articleEditorTO.getNodeName()));
    }

    @ClientCallable
    private void returnToArticleCallback() {
        UI.getCurrent().navigate(ArticlesViewer.class,
                URLIdentifierUtils.createURLIdentifier(articleEditorTO.getExistingArticleId(),
                        articleEditorTO.getDraftName()));
    }

    /**
     * Zavolá vrácení se na článek
     */
    private void returnToArticle() {
        UIUtils.removeOnbeforeunloadWarning().then(e -> returnToArticleCallback());
    }

    /**
     * zavolání vrácení se na kategorii
     */
    private void returnToNode() {
        UIUtils.removeOnbeforeunloadWarning().then(e -> returnToNodeCallback());
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