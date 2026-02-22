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
import cz.gattserver.grass.core.services.SecurityService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.pages.MainView;
import cz.gattserver.grass.core.ui.pages.NodePage;
import cz.gattserver.grass.core.ui.pages.template.ContentViewer;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlDiv;

import java.io.Serial;

@Route(value = "articles", layout = MainView.class)
public class ArticlesViewer extends Div implements HasUrlParameter<String>, HasDynamicTitle, BeforeLeaveListener {

    private ArticleService articleService;
    private SecurityService securityService;

    private ArticleTO articleTO;

    @Override
    public String getPageTitle() {
        return articleTO.name();
    }

    public ArticlesViewer(ArticleService articleFacade, SecurityService securityService,
                          ArticleService articleService) {
        this.articleService = articleFacade;
        this.securityService = securityService;
        this.articleService = articleService;
    }

    @Override
    public void setParameter(BeforeEvent event, String parameter) {
        URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
        if (identifier == null) throw new GrassPageException(404);

        articleTO = articleService.getArticleForDetail(identifier.getId(), securityService.getCurrentUser().getId(),
                securityService.getCurrentUser().isAdmin());
        if (articleTO == null) throw new GrassPageException(404);

        // RESCUE -- tohle by se normálně stát nemělo, ale umožňuje to aspoň
        // vyřešit stav, ve kterém existuje takovýto nezobrazitelný obsah
        if (articleTO.contentNodeId() == null) {
            articleService.deleteArticle(articleTO.id());
            UI.getCurrent().navigate(MainView.class);
        }

        // CSS resources
        for (String css : articleTO.pluginCSSResources()) {
            // není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
            // obejít problém se závislosí pluginů na úložišti theme apod. a
            // přitom umožnit aby se CSS odkazovali na externí zdroje
            if (!css.toLowerCase().startsWith("http://") || !css.toLowerCase().startsWith("https://"))
                css = UIUtils.getContextPath() + "/" + css;
            UIUtils.loadCSS(css);
        }

        removeAll();
        add(new ContentViewer(createHTMLDiv(), articleTO, e -> onDeleteOperation(), e -> UI.getCurrent()
                .navigate(ArticlesEditorPage.class, DefaultContentOperations.EDIT.withParameter(parameter)),
                new RouterLink(articleTO.name(), ArticlesViewer.class, parameter)));

        String jsInitDivId = "grass-js-init-div";
        Div jsInitDiv = new Div() {

            @Serial
            private static final long serialVersionUID = 7826406055666115094L;

            @ClientCallable
            private void initJS() {
                // JS resources
                int jsResourcesSize = articleTO.pluginJSResources().size();
                int jsCodesSize = articleTO.pluginJSCodes().size();

                JScriptItem[] jsResourcesArr = new JScriptItem[jsResourcesSize + jsCodesSize];
                int i = 0;
                for (String resource : articleTO.pluginJSResources())
                    jsResourcesArr[i++] = new JScriptItem(resource);
                for (String code : articleTO.pluginJSCodes())
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

    private HtmlDiv createHTMLDiv() {
        HtmlDiv content = new HtmlDiv(articleTO.outputHTML());
        content.setWidthFull();
        content.addClassName("article-content");
        return content;
    }

    protected void onDeleteOperation() {
        try {
            articleService.deleteArticle(articleTO.id());
            UI.getCurrent().navigate(NodePage.class,
                    URLIdentifierUtils.createURLIdentifier(articleTO.parentId(), articleTO.parentName()));
        } catch (Exception e) {
            // Pokud ne, otevři warn okno a při
            // potvrzení jdi na kategorii
            WarnDialog warnDialog = new WarnDialog("Smazání článku se nezdařilo.");
            warnDialog.open();
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        // TODO unload JS + CSS
    }
}