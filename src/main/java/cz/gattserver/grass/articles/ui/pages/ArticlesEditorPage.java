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
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.shared.Registration;
import cz.gattserver.common.spring.SpringContextHelper;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.CopyTagsFromContentChooseDialog;
import cz.gattserver.grass.articles.AttachmentsOperationResult;
import cz.gattserver.grass.articles.config.ArticlesConfiguration;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.util.PartsFinder;
import cz.gattserver.grass.articles.editor.parser.util.Result;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticlePayloadTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.AttachmentTO;
import cz.gattserver.grass.articles.plugins.register.PluginRegisterService;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.articles.ui.dialogs.DraftMenuDialog;
import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.services.ContentNodeService;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.services.NodeService;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlSpan;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.Resource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@PageTitle("Editor článku")
@Route(value = "articles-editor", layout = MainView.class)
public class ArticlesEditorPage extends Div implements HasUrlParameter<String>, BeforeLeaveObserver {

    private static final long serialVersionUID = -5107777679764121445L;

    private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);
    private final NodeService nodeService;

    private ArticleService articleService;
    private ContentTagService contentTagFacade;
    private ContentNodeService contentNodeService;
    private PluginRegisterService pluginRegister;
    private SecurityService securityService;

    private Grid<AttachmentTO> grid;

    private NodeOverviewTO node;

    private TextArea articleTextArea;
    private TokenField articleKeywords;
    private TextField articleNameField;
    private Checkbox publicatedCheckBox;

    private String attachmentsDirId;
    private Long existingArticleId;
    private String existingArticleName;
    private Long existingDraftId;
    private Integer partNumber;

    private Span autosaveLabel;

    private Result parts;
    private Registration articleTextAreaFocusRegistration;

    private String operationToken;
    private String identifierToken;
    private String partNumberToken;

    private ComponentFactory componentFactory;

    private boolean leaving;

    public ArticlesEditorPage(ArticleService articleService, ContentTagService contentTagFacade,
                              ContentNodeService contentNodeService, PluginRegisterService pluginRegisterService,
                              SecurityService securityService, NodeService nodeService) {
        this.articleService = articleService;
        this.contentTagFacade = contentTagFacade;
        this.contentNodeService = contentNodeService;
        this.pluginRegister = pluginRegisterService;
        this.securityService = securityService;
        this.nodeService = nodeService;

        componentFactory = new ComponentFactory();
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (!SpringContextHelper.getBean(SecurityService.class).getCurrentUser().getRoles().contains(CoreRole.AUTHOR))
            throw new GrassPageException(403, "Nemáte oprávnění na tuto operaci");

        String[] chunks = parameter.split("/");
        if (chunks.length > 0) operationToken = chunks[0];
        if (chunks.length > 1) identifierToken = chunks[1];
        if (chunks.length > 2) partNumberToken = chunks[2];

        removeAll();

        Div leftContentLayout = componentFactory.createLeftColumnLayout();
        createLeftColumnContent(leftContentLayout);
        add(leftContentLayout);

        Div rightContentLayout = componentFactory.createRightColumnLayout();
        createRightColumnContent(rightContentLayout);
        add(rightContentLayout);

        // odchod mimo Vaadin routing není možné nijak odchytit, jediná možnost je moužít native browser JS
        UIUtils.addOnbeforeunloadWarning();
    }

    private void populateByExistingArticle(ArticleTO article, String partNumberToken) {
        node = article.getContentNode().getParent();
        existingArticleId = article.getId();
        existingArticleName = article.getContentNode().getName();
        attachmentsDirId = article.getAttachmentsDirId();
        articleNameField.setValue(article.getContentNode().getName());

        for (ContentTagOverviewTO tagDTO : article.getContentNode().getContentTags())
            articleKeywords.addToken(tagDTO.getName());

        publicatedCheckBox.setValue(article.getContentNode().isPublicated());

        if (partNumberToken != null && (partNumber = Integer.valueOf(partNumberToken)) >= 0) {
            try {
                parts = PartsFinder.findParts(
                        new ByteArrayInputStream(article.getText().getBytes(StandardCharsets.UTF_8)), partNumber);
            } catch (IOException e) {
                throw new GrassException("Parsování cesty se nezdařilo", e);
            }
            articleTextArea.setValue(parts.getTargetPart());
        } else {
            articleTextArea.setValue(article.getText());
        }
    }

    private void checkAuthorization(ArticleTO article) {
        // má oprávnění upravovat tento článek?
        if (article != null && !article.getContentNode().getAuthor().equals(securityService.getCurrentUser()) &&
                !securityService.getCurrentUser().isAdmin()) throw new GrassPageException(403);
    }

    private void defaultCreateContent() {
        parts = null;
        ArticleTO article = null;

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
            node = nodeService.getNodeByIdForOverview(identifier.getId());
            articleNameField.setValue("");
            articleTextArea.setValue("");
            publicatedCheckBox.setValue(true);
        } else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
            article = articleService.getArticleForDetail(identifier.getId());
            populateByExistingArticle(article, partNumberToken);
        } else {
            logger.debug("Neznámá operace: {}", operationToken);
            throw new GrassPageException(404);
        }

        checkAuthorization(article);
    }

    private void draftCreateContent(List<ArticleDraftOverviewTO> drafts) {
        new DraftMenuDialog(drafts) {
            private static final long serialVersionUID = 1040472008288522032L;

            @Override
            protected void onChoose(ArticleDraftOverviewTO draft) {
                populateByExistingDraft(draft);
            }

            @Override
            protected void onCancel() {
                // nebyl vybrán žádný draft, pokračuj výchozím otevřením
                // editoru (new/edit)
                defaultCreateContent();
            }
        }.open();
    }

    private void populateByExistingDraft(ArticleDraftOverviewTO draft) {
        parts = null;
        ArticleTO article = null;

        existingDraftId = draft.getId();

        node = draft.getContentNode().getParent();
        articleNameField.setValue(draft.getContentNode().getName());
        for (ContentTagOverviewTO tagDTO : draft.getContentNode().getContentTags())
            articleKeywords.addToken(tagDTO.getName());
        publicatedCheckBox.setValue(draft.getContentNode().isPublicated());
        articleTextArea.setValue(draft.getText());
        attachmentsDirId = draft.getAttachmentsDirId();

        // jedná se o draft již existujícího obsahu?
        if (draft.getContentNode().getDraftSourceId() != null) {
            article = articleService.getArticleForDetail(draft.getContentNode().getDraftSourceId());
            existingArticleId = article.getId();
            existingArticleName = article.getContentNode().getName();

            // Úprava části článku může být pouze u existujícího
            // článku
            if (draft.getPartNumber() != null) {
                partNumber = draft.getPartNumber();
                try {
                    // parts se musí krájet z původního obsahu,
                    // protože v draftu je teď jenom ta část
                    parts = PartsFinder.findParts(
                            new ByteArrayInputStream(article.getText().getBytes(StandardCharsets.UTF_8)), partNumber);
                } catch (IOException e) {
                    throw new GrassPageException(500, e);
                }
            }
        }
    }

    private String createTextareaGetJS() {
        return "let ta = $(\"vaadin-text-area\")[0].children[2];";
    }

    private String createTextareaGetSelectionJS() {
        return "let start = ta.selectionStart;" + "let finish = ta.selectionEnd;";
    }

    protected void createLeftColumnContent(Div layout) {
        List<String> families = new ArrayList<>(pluginRegister.getRegisteredFamilies());
        Collections.sort(families, (o1, o2) -> {
            if (o1 == null) {
                return o2 == null ? 0 : "".compareTo(o2);
            } else {
                if (o2 == null) return o1.compareTo(""); // druhý ber jako prázdný
                else return o1.compareTo(o2); // ani jeden není null
            }
        });

        // Projdi zaregistrované pluginy a vytvoř menu nástrojů
        for (int i = 0; i < families.size(); i++) {
            String family = families.get(i);
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

                if (resourceBundle.getImagePath() != null) {
                    Button btn = new Button(resourceBundle.getDescription(),
                            new Image(resourceBundle.getImagePath(), resourceBundle.getDescription()), clickListener);
                    btn.setTooltipText(resourceBundle.getTag());
                    familyToolsLayout.add(btn);
                } else {
                    Button btn = new Button(resourceBundle.getDescription(), clickListener);
                    btn.getElement().setProperty("title", resourceBundle.getTag());
                    familyToolsLayout.add(btn);
                }
            }
        }

        layout.getStyle().set("width", "420px").set("margin-left", "-200px");
    }

    private Button createPreviewButton() {
        Button previewButton = componentFactory.createPreviewButton(event -> {
            try {
                String draftName = saveArticleDraft(true);
                String url = RouteConfiguration.forRegistry(ComponentUtil.getRouter(this).getRegistry())
                        .getUrl(ArticlesViewer.class,
                                URLIdentifierUtils.createURLIdentifier(existingDraftId, draftName));
                UI.getCurrent().getPage().open(url, "_blank");
            } catch (Exception e) {
                logger.error("Při ukládání náhledu článku došlo k chybě", e);
            }
        });
        return previewButton;
    }

    private Button createSaveButton() {
        Button saveButton = componentFactory.createSaveButton(event -> {
            if (!isFormValid()) return;
            if (saveOrUpdateArticle()) {
                UIUtils.showSilentInfo(
                        ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku proběhla úspěšně" :
                                "Uložení článku proběhlo úspěšně");
            } else {
                UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila" :
                        "Uložení článku se nezdařilo");
            }
        });
        return saveButton;
    }

    private Button createSaveAndCloseButton() {
        Button saveAndCloseButton = componentFactory.createSaveAndCloseButton(event -> {
            if (!isFormValid()) return;
            if (saveOrUpdateArticle()) {
                leaving = true;
                returnToArticle();
            } else {
                UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "Úprava článku se nezdařila" :
                        "Uložení článku se nezdařilo");
            }
        });
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);
        return saveAndCloseButton;
    }

    private Button createCancelButton() {
        Button cancelButton = componentFactory.createStornoButton(event -> new ConfirmDialog(
                "Opravdu si přejete zavřít editor článku? Veškeré neuložené změny budou ztraceny.", e -> {
            // ruším úpravu existujícího článku (vracím se na
            // článek), nebo nového (vracím se do kategorie) ?
            if (existingArticleId != null) {
                returnToArticle();
            } else {
                returnToNode();
            }
        }).open());
        return cancelButton;
    }

    private String saveArticleDraft(boolean asPreview) {
        String draftName = articleNameField.getValue();
        ArticlePayloadTO payload =
                new ArticlePayloadTO(draftName, articleTextArea.getValue(), articleKeywords.getValues(),
                        publicatedCheckBox.getValue(), attachmentsDirId, UIUtils.getContextPath());
        if (existingDraftId == null) {
            if (existingArticleId == null) {
                existingDraftId =
                        articleService.saveDraft(payload, node.getId(), securityService.getCurrentUser().getId(),
                                asPreview);
            } else {
                existingDraftId = articleService.saveDraftOfExistingArticle(payload, node.getId(),
                        securityService.getCurrentUser().getId(), partNumber, existingArticleId, asPreview);
            }
        } else {
            if (existingArticleId == null) {
                articleService.modifyDraft(existingDraftId, payload, asPreview);
            } else {
                articleService.modifyDraftOfExistingArticle(existingDraftId, payload, partNumber, existingArticleId,
                        asPreview);
            }
        }
        return draftName;
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
        String result = preText + '\t';
        int finishShift = 1;
        for (int i = 0; i < tabbedText.length(); i++) {
            char c = tabbedText.charAt(i);
            result += c;
            if (c == '\n' && i != tabbedText.length() - 1) {
                result += '\t';
                finishShift++;
            }
        }
        result += postText;
        articleTextArea.setValue(result);
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
            saveArticleDraft(false);
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
        int size = articleService.listCount(attachmentsDirId);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(
                q -> articleService.listing(attachmentsDirId, q.getOffset(), q.getLimit(), q.getSortOrders()),
                q -> size));
    }

    private String getDownloadLink(AttachmentTO item) {
        StringBuilder sb = new StringBuilder();
        sb.append(UIUtils.getURLBase());
        sb.append("/");
        sb.append(ArticlesConfiguration.ATTACHMENTS_PATH);
        if (!sb.toString().endsWith("/")) sb.append("/");
        sb.append(attachmentsDirId);
        sb.append("/");
        sb.append(item.getName());

        return sb.toString();
    }

    private void handleDownloadAction(AttachmentTO item) {
        UI.getCurrent().getPage().open(getDownloadLink(item));
    }

    private void handleDeleteAction(AttachmentTO to) {
        new ConfirmDialog(e -> {
            AttachmentsOperationResult result = articleService.deleteAttachment(attachmentsDirId, to.getName());
            switch (result.getState()) {
                case SUCCESS:
                    attachmentsDirId = result.getAttachmentDirId();
                    populateGrid();
                    break;
                default:
                    UIUtils.showWarning(
                            "Soubor '" + to.getName() + "' nebylo možné smazat - došlo k systémové chybě" + ".");
            }
        }).open();
    }

    private void handleInsertAction(AttachmentTO to) {
        String url = getDownloadLink(to);
        String js = createTextareaGetJS() + createTextareaGetSelectionJS() + "$0.$server.handleSelection(\"" + url +
                "\", \"\", start, finish)";
        UI.getCurrent().getPage().executeJs(js, getElement());
    }

    private void createAttachmentsGrid(Div layout) {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(SelectionMode.NONE);
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(grid);

        grid.setHeight("200px");

        grid.addColumn(AttachmentTO::getName).setHeader("Název").setFlexGrow(100).setSortProperty("name");

        grid.addColumn(AttachmentTO::getSize).setHeader("Velikost").setTextAlign(ColumnTextAlign.END).setWidth("80px")
                .setFlexGrow(0).setSortProperty("size");

        grid.addColumn(new ComponentRenderer<>(
                        to -> componentFactory.createInlineButton("Stáhnout", e -> handleDownloadAction(to))))
                .setHeader("Stažení").setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(
                        to -> componentFactory.createInlineButton("Vložit", e -> handleInsertAction(to)))).setHeader("Vložit")
                .setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<>(
                        to -> componentFactory.createInlineButton("Smazat", e -> handleDeleteAction(to)))).setHeader("Smazat")
                .setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new LocalDateTimeRenderer<>(AttachmentTO::getLastModified, "d. MM. yyyy HH:mm"))
                .setHeader("Upraveno").setAutoWidth(true).setTextAlign(ColumnTextAlign.END)
                .setSortProperty("lastModified");

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1) handleInsertAction(e.getItem());
        });

        GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();

        Upload upload = new Upload(buffer);
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.addSucceededListener(event -> {
            AttachmentsOperationResult result =
                    articleService.saveAttachment(attachmentsDirId, buffer.getInputStream(event.getFileName()),
                            event.getFileName());
            switch (result.getState()) {
                case SUCCESS:
                    attachmentsDirId = result.getAttachmentDirId();
                    populateGrid();
                    break;
                case ALREADY_EXISTS:
                    UIUtils.showWarning("Soubor '" + event.getFileName() +
                            "' nebylo možné uložit - soubor s tímto názvem již existuje.");
                    break;
                default:
                    UIUtils.showWarning(
                            "Soubor '" + event.getFileName() + "' nebylo možné uložit - došlo k systémové chybě.");
            }
            if (existingDraftId == null) saveArticleDraft(false);
        });
        layout.add(upload);
    }

    protected void createRightColumnContent(Div layout) {
        articleNameField = new TextField();
        articleNameField.setValueChangeMode(ValueChangeMode.EAGER);

        CallbackDataProvider.FetchCallback<String, String> fetchItemsCallback =
                q -> contentTagFacade.findByFilter(q.getFilter().get(), q.getOffset(), q.getLimit()).stream();
        CallbackDataProvider.CountCallback<String, String> serializableFunction =
                q -> contentTagFacade.countByFilter(q.getFilter().get());
        articleKeywords = new TokenField(fetchItemsCallback, serializableFunction);
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

        List<ArticleDraftOverviewTO> drafts = articleService.getDraftsForUser(securityService.getCurrentUser().getId());
        if (drafts.isEmpty()) {
            // nejsou-li v DB žádné pro přihlášeného uživatele viditelné drafty
            // článků, otevři editor dle operace (new/edit)
            defaultCreateContent();
        } else {
            // pokud jsou nalezeny drafty k dokončení, nabídni je k výběru
            draftCreateContent(drafts);
        }

        layout.add(new H3("Název článku"));
        layout.add(articleNameField);
        articleNameField.setWidthFull();

        // label
        layout.add(new H3("Klíčová slova"));

        // menu tagů + textfield tagů
        layout.add(articleKeywords);

        Button copyFromContentButton = componentFactory.createCopyFromContentButton(
                e -> new CopyTagsFromContentChooseDialog(list -> list.forEach(articleKeywords::addToken)).open());
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

        // Uložit a zavřít
        Button saveAndCloseButton = createSaveAndCloseButton();
        buttonLayout.add(saveAndCloseButton);

        // Zrušit
        Button cancelButton = createCancelButton();
        buttonLayout.add(cancelButton);

        // Auto-ukládání
        Span autosaveLabel = createAutosaveLabel();
        buttonLayout.add(autosaveLabel);

        populateGrid();
    }

    private boolean isFormValid() {
        String name = articleNameField.getValue();
        if (name == null || name.isEmpty()) {
            UIUtils.showWarning("Název článku nemůže být prázdný");
            return false;
        }
        return true;
    }

    private boolean saveOrUpdateArticle() {
        try {
            String text;
            if (parts != null) {
                StringBuilder builder = new StringBuilder();
                builder.append(parts.getPrePart());
                builder.append(articleTextArea.getValue());
                builder.append(parts.getPostPart());
                text = builder.toString();
            } else {
                text = articleTextArea.getValue();
            }

            ArticlePayloadTO payload =
                    new ArticlePayloadTO(articleNameField.getValue(), text, articleKeywords.getValues(),
                            publicatedCheckBox.getValue(), attachmentsDirId, UIUtils.getContextPath());
            if (existingArticleId == null) {
                // byl uložen článek, od teď eviduj draft, jako draft
                // existujícího obsahu
                existingArticleId =
                        articleService.saveArticle(payload, node.getId(), securityService.getCurrentUser().getId());
                this.existingArticleName = articleNameField.getValue();
            } else {
                articleService.modifyArticle(existingArticleId, payload, partNumber);
            }
            return true;
        } catch (Exception e) {
            logger.error("Při ukládání článku došlo k chybě", e);
        }
        return false;
    }

    @ClientCallable
    private void returnToNodeCallback() {
        UI.getCurrent().navigate(NodePage.class, URLIdentifierUtils.createURLIdentifier(node.getId(), node.getName()));
    }

    @ClientCallable
    private void returnToArticleCallback() {
        UI.getCurrent().navigate(ArticlesViewer.class,
                URLIdentifierUtils.createURLIdentifier(existingArticleId, existingArticleName));
    }

    /**
     * Zavolá vrácení se na článek
     */
    private void returnToArticle() {
        // smaž draft, ponechej přílohy, pokud k draftu existuje článek
        if (existingDraftId != null) articleService.deleteArticle(existingDraftId, existingArticleId == null);
        UI.getCurrent().getPage().executeJs("window.onbeforeunload = null;").then(e -> returnToArticleCallback());
    }

    /**
     * zavolání vrácení se na kategorii
     */
    private void returnToNode() {
        // smaž draft, ponechej přílohy, pokud k draftu existuje článek
        if (existingDraftId != null) articleService.deleteArticle(existingDraftId, existingArticleId == null);
        UI.getCurrent().getPage().executeJs("window.onbeforeunload = null;").then(e -> returnToNodeCallback());
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