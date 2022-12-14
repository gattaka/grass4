package cz.gattserver.grass.articles.ui.pages;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox.FetchItemsCallback;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableFunction;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.shared.Registration;
import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
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
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentTagOverviewTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.ContentTagService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.components.button.ImageButton;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.TwoColumnPage;
import cz.gattserver.grass.core.ui.util.ButtonLayout;
import cz.gattserver.grass.core.ui.util.GrassMultiFileBuffer;
import cz.gattserver.grass.core.ui.util.TokenField;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlSpan;
import cz.gattserver.common.vaadin.LinkButton;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Route("articles-editor")
@PageTitle("Editor ??l??nku")
public class ArticlesEditorPage extends TwoColumnPage implements HasUrlParameter<String> {

    private static final long serialVersionUID = -5107777679764121445L;

    private static final Logger logger = LoggerFactory.getLogger(ArticlesEditorPage.class);

    private static final String CLOSE_JS_DIV_ID = "close-js-div";
    private static final String HANDLER_JS_DIV_ID = "handler-js-div";

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ContentTagService contentTagFacade;

    @Autowired
    private PluginRegisterService pluginRegister;

    @Resource(name = "articlesViewerPageFactory")
    private PageFactory articlesViewerPageFactory;

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

    private Result parts;
    private Registration articleTextAreaFocusRegistration;

    private String operationToken;
    private String identifierToken;
    private String partNumberToken;

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        String[] chunks = parameter.split("/");
        if (chunks.length > 0)
            operationToken = chunks[0];
        if (chunks.length > 1)
            identifierToken = chunks[1];
        if (chunks.length > 2)
            partNumberToken = chunks[2];

        init();

        UI.getCurrent().getPage().executeJs(
                "window.onbeforeunload = function() { return \"Opravdu si p??ejete ukon??it editor a odej??t - rozpracovan?? data nejsou ulo??ena ?\" };");
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
                throw new GrassPageException(500, e);
            }
            articleTextArea.setValue(parts.getTargetPart());
        } else {
            articleTextArea.setValue(article.getText());
        }
    }

    private void checkAuthorization(ArticleTO article) {
        // m?? opr??vn??n?? upravovat tento ??l??nek?
        if (article != null && !article.getContentNode().getAuthor().equals(getUser()) && !getUser().isAdmin())
            throw new GrassPageException(403);
    }

    private void defaultCreateContent(Div customlayout) {
        parts = null;
        ArticleTO article = null;

        if (operationToken == null || identifierToken == null) {
            logger.debug("Chyb?? operace nebo identifik??tor c??le");
            throw new GrassPageException(404);
        }

        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(identifierToken);
        if (identifier == null) {
            logger.debug("Nezda??ilo se vyt????it URL identifik??tor z ??et??zce: {}", identifierToken);
            throw new GrassPageException(404);
        }

        // operace ?
        if (operationToken.equals(DefaultContentOperations.NEW.toString())) {
            node = nodeFacade.getNodeByIdForOverview(identifier.getId());
            articleNameField.setValue("");
            articleTextArea.setValue("");
            publicatedCheckBox.setValue(true);
        } else if (operationToken.equals(DefaultContentOperations.EDIT.toString())) {
            article = articleService.getArticleForDetail(identifier.getId());
            populateByExistingArticle(article, partNumberToken);
        } else {
            logger.debug("Nezn??m?? operace: {}", operationToken);
            throw new GrassPageException(404);
        }

        checkAuthorization(article);
        super.createCenterElements(customlayout);
    }

    private void draftCreateContent(Div customlayout, List<ArticleDraftOverviewTO> drafts) {
        new DraftMenuDialog(drafts) {
            private static final long serialVersionUID = 1040472008288522032L;

            @Override
            protected void onChoose(ArticleDraftOverviewTO draft) {
                populateByExistingDraft(customlayout, draft);
            }

            @Override
            protected void onCancel() {
                // nebyl vybr??n ????dn?? draft, pokra??uj v??choz??m otev??en??m
                // editoru (new/edit)
                defaultCreateContent(customlayout);
            }
        }.open();
    }

    private void populateByExistingDraft(Div customlayout, ArticleDraftOverviewTO draft) {
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

        // jedn?? se o draft ji?? existuj??c??ho obsahu?
        if (draft.getContentNode().getDraftSourceId() != null) {
            article = articleService.getArticleForDetail(draft.getContentNode().getDraftSourceId());
            existingArticleId = article.getId();
            existingArticleName = article.getContentNode().getName();

            // ??prava ????sti ??l??nku m????e b??t pouze u existuj??c??ho
            // ??l??nku
            if (draft.getPartNumber() != null) {
                partNumber = draft.getPartNumber();
                try {
                    // parts se mus?? kr??jet z p??vodn??ho obsahu,
                    // proto??e v draftu je te?? jenom ta ????st
                    parts = PartsFinder.findParts(
                            new ByteArrayInputStream(article.getText().getBytes(StandardCharsets.UTF_8)), partNumber);
                } catch (IOException e) {
                    throw new GrassPageException(500, e);
                }
            }
        }

        ArticlesEditorPage.super.createCenterElements(customlayout);
    }

    @Override
    protected void createCenterElements(Div customlayout) {
        articleNameField = new TextField();
        articleNameField.setValueChangeMode(ValueChangeMode.EAGER);

        FetchItemsCallback<String> fetchItemsCallback = (filter, offset, limit) -> contentTagFacade
                .findByFilter(filter, offset, limit).stream();
        SerializableFunction<String, Integer> serializableFunction = filter -> contentTagFacade.countByFilter(filter);
        articleKeywords = new TokenField(fetchItemsCallback, serializableFunction);
        articleKeywords.isEnabled();
        articleKeywords.setPlaceholder("kl????ov?? slovo");

        articleTextArea = new TextArea();
        articleTextArea.setHeight("30em");
        articleTextArea.setWidthFull();
        articleTextArea.setValueChangeMode(ValueChangeMode.EAGER);
        publicatedCheckBox = new Checkbox();

        // zav??d??n?? listener pro JS listener akc?? jako je veps??n?? tabul??toru
        articleTextAreaFocusRegistration = articleTextArea.addFocusListener(event -> {
            String js = createTextareaGetJS() + "ta.addEventListener('keydown', function(e) {"
                    /*				*/ + "let keyCode = e.keyCode || e.which;"
                    /*				*/ + "if (keyCode == 9) {"
                    /*					*/ + "e.preventDefault();"
                    /*					*/ + createTextareaGetSelectionJS()
                    /*					*/ + "document.getElementById('" + HANDLER_JS_DIV_ID
                    + "').$server.handleTab(start, finish, ta.value);"
                    /*				*/ + "}"
                    /*			*/ + "}, false);";
            UI.getCurrent().getPage().executeJs(js);
            // je pot??eba jenom jedno pro registraci
            articleTextAreaFocusRegistration.remove();
        });
        // aby se zaregistroval JS listener
        articleTextArea.focus();

        List<ArticleDraftOverviewTO> drafts = articleService.getDraftsForUser(getUser().getId());
        if (drafts.isEmpty()) {
            // nejsou-li v DB ????dn?? pro p??ihl????en??ho u??ivatele viditeln?? drafty
            // ??l??nk??, otev??i editor dle operace (new/edit)
            defaultCreateContent(customlayout);
        } else {
            // pokud jsou nalezeny drafty k dokon??en??, nab??dni je k v??b??ru
            draftCreateContent(customlayout, drafts);
        }
    }

    private String createTextareaGetJS() {
        return "let sr = $(\"vaadin-text-area\")[0].shadowRoot;"
                + "let ta = sr.children[1].children[1].children[1].children[0];";
    }

    private String createTextareaGetSelectionJS() {
        return "let start = ta.selectionStart;" + "let finish = ta.selectionEnd;";
    }

    @Override
    protected void createLeftColumnContent(Div layout) {
        layout.getStyle().set("text-align", "center");
        List<String> families = new ArrayList<>(pluginRegister.getRegisteredFamilies());
        Collections.sort(families, (o1, o2) -> {
            if (o1 == null) {
                return o2 == null ? 0 : "".compareTo(o2);
            } else {
                if (o2 == null)
                    return o1.compareTo(""); // druh?? ber jako pr??zdn??
                else
                    return o1.compareTo(o2); // ani jeden nen?? null
            }
        });

        // JS handler
        Div handlerJsDiv = new Div() {
            private static final long serialVersionUID = -7319482130016598549L;

            @ClientCallable
            private void handleSelection(String prefix, String suffix, int start, int end) {
                String origtext = articleTextArea.getValue();
                String text = origtext.substring(0, start) + prefix;
                int pos = text.length();
                text = text + origtext.substring(start, end) + suffix;
                if (end < origtext.length())
                    text += origtext.substring(end);
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
                UI.getCurrent().getPage().executeJs(
                        createTextareaGetJS() + "$(ta).get(0).setSelectionRange(" + start + "," + finish + ");");
            }
        };
        handlerJsDiv.setId(HANDLER_JS_DIV_ID);
        layout.add(handlerJsDiv);

        // Projdi zaregistrovan?? pluginy a vytvo?? menu n??stroj??
        for (int i = 0; i < families.size(); i++) {
            String family = families.get(i);
            ButtonLayout familyToolsLayout = new ButtonLayout();
            Span headerSpan = new Span();
            headerSpan.add(family);
            String desc = pluginRegister.getFamilyDescription(family);
            if (StringUtils.isNotBlank(desc))
                headerSpan.add(new HtmlSpan(" " + desc));
            Div header = new Div(headerSpan);
            header.getStyle().set("border-bottom", "2px solid #aaa").set("line-height", "0").set("margin",
                    i == 0 ? "10px 0" : "20px 0 10px 0");
            headerSpan.getStyle().set("cz/gattserver/grass/articles/plugins/basic/color", "#888").set("font-size", "11pt").set("font-weight", "600")
                    .set("background", "#f4f1e6").set("padding", "0 10px");
            layout.add(header);
            layout.add(familyToolsLayout);

            List<EditorButtonResourcesTO> resourcesBundles = new ArrayList<>(
                    pluginRegister.getTagResourcesByFamily(family));
            Collections.sort(resourcesBundles);

            for (EditorButtonResourcesTO resourceBundle : resourcesBundles) {
                String js = createTextareaGetJS() + createTextareaGetSelectionJS() + "document.getElementById('"
                        + HANDLER_JS_DIV_ID + "').$server.handleSelection(\"" + resourceBundle.getPrefix() + "\", \""
                        + resourceBundle.getSuffix() + "\", start, finish)";

                if (resourceBundle.getImage() != null) {
                    ImageButton btn = new ImageButton(resourceBundle.getDescription(),
                            new Image(resourceBundle.getImage(), resourceBundle.getTag()),
                            event -> UI.getCurrent().getPage().executeJs(js));
                    btn.setTooltip(resourceBundle.getTag());
                    familyToolsLayout.add(btn);
                } else {
                    Button btn = new Button(resourceBundle.getDescription(),
                            event -> UI.getCurrent().getPage().executeJs(js));
                    btn.getElement().setProperty("title", resourceBundle.getTag());
                    familyToolsLayout.add(btn);
                }
            }
        }

        layout.getStyle().set("width", "420px").set("margin-left", "-200px");
    }

    private Button createPreviewButton() {
        Button previewButton = new ImageButton("N??hled", ImageIcon.DOCUMENT_16_ICON, event -> {
            try {
                String draftName = saveArticleDraft(true);
                UI.getCurrent().getPage().open(getPageURL(articlesViewerPageFactory,
                        URLIdentifierUtils.createURLIdentifier(existingDraftId, draftName)));
            } catch (Exception e) {
                logger.error("P??i ukl??d??n?? n??hledu ??l??nku do??lo k chyb??", e);
            }
        });
        return previewButton;
    }

    private Button createSaveButton() {
        Button saveButton = new ImageButton("Ulo??it", ImageIcon.SAVE_16_ICON, event -> {
            if (!isFormValid())
                return;
            if (saveOrUpdateArticle()) {
                UIUtils.showSilentInfo(
                        ArticlesEditorPage.this.existingArticleId != null ? "??prava ??l??nku prob??hla ??sp????n??"
                                : "Ulo??en?? ??l??nku prob??hlo ??sp????n??");
            } else {
                UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "??prava ??l??nku se nezda??ila"
                        : "Ulo??en?? ??l??nku se nezda??ilo");
            }
        });
        return saveButton;
    }

    private Button createSaveAndCloseButton() {
        Button saveAndCloseButton = new ImageButton("Ulo??it a zav????t", ImageIcon.SAVE_16_ICON);
        saveAndCloseButton.addClickListener(event -> {
            // aby se provedl blur na ostatn??ch elementech a poslaly se hodnoty
            if (!isFormValid())
                return;
            if (saveOrUpdateArticle()) {
                // Tady nem?? cena d??vat infowindow
                returnToArticle();
            } else {
                UIUtils.showWarning(ArticlesEditorPage.this.existingArticleId != null ? "??prava ??l??nku se nezda??ila"
                        : "Ulo??en?? ??l??nku se nezda??ilo");
            }
        });
        saveAndCloseButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL).setBrowserDefaultAllowed(false);
        return saveAndCloseButton;
    }

    private Button createCancelButton() {
        Button cancelButton = new ImageButton("Zru??it", ImageIcon.DELETE_16_ICON,
                event -> new ConfirmDialog(
                        "Opravdu si p??ejete zav????t editor ??l??nku ? Ve??ker?? neulo??en?? zm??ny budou ztraceny.", e -> {
                    // ru????m ??pravu existuj??c??ho ??l??nku (vrac??m se na
                    // ??l??nek), nebo nov??ho (vrac??m se do kategorie) ?
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
        ArticlePayloadTO payload = new ArticlePayloadTO(draftName, articleTextArea.getValue(),
                articleKeywords.getValues(), publicatedCheckBox.getValue(), attachmentsDirId, getContextPath());
        if (existingDraftId == null) {
            if (existingArticleId == null) {
                existingDraftId = articleService.saveDraft(payload, node.getId(), getUser().getId(), asPreview);
            } else {
                existingDraftId = articleService.saveDraftOfExistingArticle(payload, node.getId(), getUser().getId(),
                        partNumber, existingArticleId, asPreview);
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

    private Span createAutosaveLabel() {
        final Span autosaveLabel = new Span();
        Div autosaveJsDiv = new Div() {
            private static final long serialVersionUID = -7319482130016598549L;

            @ClientCallable
            private void autosaveCallback() {
                try {
                    saveArticleDraft(false);
                    autosaveLabel.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                            + " Automaticky ulo??eno");
                    autosaveLabel.setClassName("label-ok");
                } catch (Exception e) {
                    autosaveLabel.setText(
                            LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "Chyba ulo??en??");
                    autosaveLabel.setClassName("label-err");
                }
            }
        };

        String autosaveJsDivId = "autosave-js-div";
        autosaveJsDiv.setId(autosaveJsDivId);
        add(autosaveJsDiv);

        UI.getCurrent().getPage().executeJs("setInterval(function(){ document.getElementById('" + autosaveJsDivId
                + "').$server.autosaveCallback() }, 10000);");

        return autosaveLabel;
    }

    private void populateGrid() {
        int size = articleService.listCount(attachmentsDirId);
        grid.setDataProvider(DataProvider.fromFilteringCallbacks(
                q -> articleService.listing(attachmentsDirId, q.getOffset(), q.getLimit(), q.getSortOrders()),
                q -> size));
    }

    private String getDownloadLink(AttachmentTO item) {
        VaadinRequest vaadinRequest = VaadinRequest.getCurrent();
        VaadinServletRequest vaadinServletRequest = (VaadinServletRequest) vaadinRequest;
        String requestURI = ((VaadinServletRequest) vaadinRequest).getRequestURI();
        String fullURL = vaadinServletRequest.getRequestURL().toString();
        String urlBase = fullURL.substring(0, fullURL.length() - requestURI.length());
        String contextRootURL = UIUtils.getContextPath();
        StringBuilder sb = new StringBuilder();
        sb.append(urlBase);
        sb.append(contextRootURL);
        if (!contextRootURL.endsWith("/"))
            sb.append("/");
        sb.append(ArticlesConfiguration.ATTACHMENTS_PATH);
        if (!sb.toString().endsWith("/"))
            sb.append("/");
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
                    UIUtils.showWarning("Soubor '" + to.getName() + "' nebylo mo??n?? smazat - do??lo k syst??mov?? chyb??.");
            }
        }).open();
    }

    private void handleInsertAction(AttachmentTO to) {
        String url = getDownloadLink(to);
        String js = createTextareaGetJS() + createTextareaGetSelectionJS() + "document.getElementById('"
                + HANDLER_JS_DIV_ID + "').$server.handleSelection(\"" + url + "\", \"\", start, finish)";
        UI.getCurrent().getPage().executeJs(js);
    }

    private void createAttachmentsGrid(Div layout) {
        grid = new Grid<>();
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(SelectionMode.NONE);
        UIUtils.applyGrassDefaultStyle(grid);
        grid.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(grid);

        grid.setHeight("200px");

        grid.addColumn(AttachmentTO::getName).setHeader("N??zev").setFlexGrow(100).setSortProperty("name");

        grid.addColumn(AttachmentTO::getSize).setHeader("Velikost").setTextAlign(ColumnTextAlign.END).setWidth("80px")
                .setFlexGrow(0).setSortProperty("size");

        grid.addColumn(new ComponentRenderer<Button, AttachmentTO>(
                        to -> new LinkButton("St??hnout", e -> handleDownloadAction(to)))).setHeader("Sta??en??")
                .setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<Button, AttachmentTO>(
                        to -> new LinkButton("Vlo??it", e -> handleInsertAction(to)))).setHeader("Vlo??it")
                .setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new ComponentRenderer<Button, AttachmentTO>(
                        to -> new LinkButton("Smazat", e -> handleDeleteAction(to)))).setHeader("Smazat")
                .setTextAlign(ColumnTextAlign.CENTER).setWidth("90px").setFlexGrow(0);

        grid.addColumn(new LocalDateTimeRenderer<>(AttachmentTO::getLastModified, "d.MM.yyyy HH:mm"))
                .setHeader("Upraveno").setAutoWidth(true).setTextAlign(ColumnTextAlign.END)
                .setSortProperty("lastModified");

        grid.addItemClickListener(e -> {
            if (e.getClickCount() > 1)
                handleInsertAction(e.getItem());
        });

        GrassMultiFileBuffer buffer = new GrassMultiFileBuffer();

        Upload upload = new Upload(buffer);
        upload.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        upload.addSucceededListener(event -> {
            AttachmentsOperationResult result = articleService.saveAttachment(attachmentsDirId,
                    buffer.getInputStream(event.getFileName()), event.getFileName());
            switch (result.getState()) {
                case SUCCESS:
                    attachmentsDirId = result.getAttachmentDirId();
                    populateGrid();
                    break;
                case ALREADY_EXISTS:
                    UIUtils.showWarning("Soubor '" + event.getFileName()
                            + "' nebylo mo??n?? ulo??it - soubor s t??mto n??zvem ji?? existuje.");
                    break;
                default:
                    UIUtils.showWarning(
                            "Soubor '" + event.getFileName() + "' nebylo mo??n?? ulo??it - do??lo k syst??mov?? chyb??.");
            }
            if (existingDraftId == null)
                saveArticleDraft(false);
        });
        layout.add(upload);
    }

    @Override
    protected void createRightColumnContent(Div layout) {
        layout.add(new H2("N??zev ??l??nku"));
        layout.add(articleNameField);
        articleNameField.setWidthFull();

        // label
        layout.add(new H2("Kl????ov?? slova"));

        // menu tag?? + textfield tag??
        layout.add(articleKeywords);
        articleKeywords.addClassName(UIUtils.TOP_PULL_CSS_CLASS);

        layout.add(new H2("Obsah ??l??nku"));
        layout.add(articleTextArea);

        layout.add(new H2("P????lohy ??l??nku"));
        createAttachmentsGrid(layout);

        layout.add(new H2("Nastaven?? ??l??nku"));
        publicatedCheckBox.setLabel("Publikovat ??l??nek");
        layout.add(publicatedCheckBox);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);
        layout.add(buttonLayout);

        // N??hled
        Button previewButton = createPreviewButton();
        buttonLayout.add(previewButton);

        // Ulo??it
        Button saveButton = createSaveButton();
        buttonLayout.add(saveButton);

        // Ulo??it a zav????t
        Button saveAndCloseButton = createSaveAndCloseButton();
        buttonLayout.add(saveAndCloseButton);

        // Zru??it
        Button cancelButton = createCancelButton();
        buttonLayout.add(cancelButton);

        // Auto-ukl??d??n??
        Span autosaveLabel = createAutosaveLabel();
        buttonLayout.add(autosaveLabel);

        populateGrid();
    }

    private boolean isFormValid() {
        String name = articleNameField.getValue();
        if (name == null || name.isEmpty()) {
            UIUtils.showWarning("N??zev ??l??nku nem????e b??t pr??zdn??");
            return false;
        }
        return true;
    }

    private boolean saveOrUpdateArticle() {
        try {
            String text = null;
            if (parts != null) {
                StringBuilder builder = new StringBuilder();
                builder.append(parts.getPrePart());
                builder.append(articleTextArea.getValue());
                builder.append(parts.getPostPart());
                text = builder.toString();
            } else {
                text = articleTextArea.getValue();
            }

            ArticlePayloadTO payload = new ArticlePayloadTO(articleNameField.getValue(), text,
                    articleKeywords.getValues(), publicatedCheckBox.getValue(), attachmentsDirId, getContextPath());
            if (existingArticleId == null) {
                // byl ulo??en ??l??nek, od te?? eviduj draft, jako draft
                // existuj??c??ho obsahu
                existingArticleId = articleService.saveArticle(payload, node.getId(), getUser().getId());
                this.existingArticleName = articleNameField.getValue();
            } else {
                articleService.modifyArticle(existingArticleId, payload, partNumber);
            }
            return true;
        } catch (Exception e) {
            logger.error("P??i ukl??d??n?? ??l??nku do??lo k chyb??", e);
        }
        return false;
    }

    /**
     * Zavol?? vr??cen?? se na ??l??nek
     */
    private void returnToArticle() {
        // sma?? draft, ponechej p????lohy, pokud k draftu existuje ??l??nek
        if (existingDraftId != null)
            articleService.deleteArticle(existingDraftId, existingArticleId == null);

        Div closeJsDiv = new Div() {
            private static final long serialVersionUID = -7319482130016598549L;

            @ClientCallable
            private void closeCallback() {
                UIUtils.redirect(getPageURL(articlesViewerPageFactory,
                        URLIdentifierUtils.createURLIdentifier(existingArticleId, existingArticleName)));
            }
        };
        closeJsDiv.setId(CLOSE_JS_DIV_ID);
        add(closeJsDiv);

        UI.getCurrent().getPage()
                .executeJs("window.onbeforeunload = null; setTimeout(function(){ document.getElementById('"
                        + CLOSE_JS_DIV_ID + "').$server.closeCallback() }, 10);");
    }

    /**
     * zavol??n?? vr??cen?? se na kategorii
     */
    private void returnToNode() {
        // sma?? draft, ponechej p????lohy, pokud k draftu existuje ??l??nek
        if (existingDraftId != null)
            articleService.deleteArticle(existingDraftId, existingArticleId == null);

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

        UI.getCurrent().getPage()
                .executeJs("window.onbeforeunload = null; setTimeout(function(){ document.getElementById('"
                        + CLOSE_JS_DIV_ID + "').$server.closeCallback() }, 10);");
    }

}
