package cz.gattserver.grass.articles.ui.pages;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.*;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.template.ContentViewer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlDiv;

@Route(value = "articles", layout = MainView.class)
public class ArticlesViewer extends Div implements HasUrlParameter<String>, HasDynamicTitle, BeforeLeaveListener {

    private static final long serialVersionUID = 7511698289319715316L;

    private ArticleService articleService;
    private SecurityService securityService;

    private ArticleTO article;

    @Override
    public String getPageTitle() {
        return article.getContentNode().getName();
    }

    public ArticlesViewer(ArticleService articleFacade, SecurityService securityService, ArticleService articleService) {
        this.articleService = articleFacade;
        this.securityService = securityService;
        this.articleService = articleService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
        if (identifier == null) throw new GrassPageException(404);

        article = articleService.getArticleForDetail(identifier.getId());
        if (article == null) throw new GrassPageException(404);

        // RESCUE -- tohle by se normálně stát nemělo, ale umožňuje to aspoň
        // vyřešit stav, ve kterém existuje takovýto nezobrazitelný obsah
        if (article.getContentNode() == null) {
            articleService.deleteArticle(article.getId());
            UI.getCurrent().navigate(MainView.class);
        }

        if (!article.getContentNode().isPublicated() && !securityService.getCurrentUser().isAdmin() &&
                !article.getContentNode().getAuthor().equals(securityService.getCurrentUser()))
            throw new GrassPageException(403);

        // CSS resources
        for (String css : article.getPluginCSSResources()) {
            // není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
            // obejít problém se závislosí pluginů na úložišti theme apod. a
            // přitom umožnit aby se CSS odkazovali na externí zdroje
            if (!css.toLowerCase().startsWith("http://") || !css.toLowerCase().startsWith("https://"))
                css = UIUtils.getContextPath() + "/" + css;
            UIUtils.loadCSS(css);
        }

        removeAll();
        ContentNodeTO contentNodeTO = article.getContentNode();
        add(new ContentViewer(createContent(), contentNodeTO, e -> onDeleteOperation(), e -> UI.getCurrent()
                .navigate(ArticlesEditorPage.class, DefaultContentOperations.EDIT.withParameter(parameter)),
                new RouterLink(contentNodeTO.getName(), ArticlesViewer.class, parameter)));

        String jsInitDivId = "grass-js-init-div";
        Div jsInitDiv = new Div() {
            private static final long serialVersionUID = -5609636078016625081L;

            @ClientCallable
            private void initJS() {
                // JS resources
                int jsResourcesSize = article.getPluginJSResources().size();
                int jsCodesSize = article.getPluginJSCodes().size();

                JScriptItem[] jsResourcesArr = new JScriptItem[jsResourcesSize + jsCodesSize];
                int i = 0;
                for (String resource : article.getPluginJSResources())
                    jsResourcesArr[i++] = new JScriptItem(resource);
                for (String code : article.getPluginJSCodes())
                    jsResourcesArr[i++] = new JScriptItem(code, true);

                UIUtils.loadJS(jsResourcesArr);
            }
        };
        jsInitDiv.setId(jsInitDivId);
        jsInitDiv.addAttachListener(e -> UI.getCurrent().getPage()
                .executeJs("document.getElementById('" + jsInitDivId + "').$server.initJS();"));
        add(jsInitDiv);

        UIUtils.turnOffRouterAnchors();
    }

    private HtmlDiv createContent() {
        HtmlDiv content = new HtmlDiv(article.getOutputHTML());
        content.setWidthFull();
        content.addClassName("article-content");
        return content;
    }

    protected void onDeleteOperation() {
        ConfirmDialog confirmDialog = new ConfirmDialog("Opravdu si přejete smazat tento článek ?", event -> {
            NodeOverviewTO nodeDTO = article.getContentNode().getParent();

            // zdařilo se ? Pokud ano, otevři info okno a při
            // potvrzení jdi na kategorii
            try {
                articleService.deleteArticle(article.getId());
                UI.getCurrent().navigate(NodePage.class,
                        URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));
            } catch (Exception e) {
                // Pokud ne, otevři warn okno a při
                // potvrzení jdi na kategorii
                WarnDialog warnDialog = new WarnDialog("Smazání článku se nezdařilo.");
                warnDialog.open();
            }
        });
        confirmDialog.open();
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        // TODO unload JS + CSS
    }
}