package cz.gattserver.grass.articles.ui.pages;

import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import cz.gattserver.common.vaadin.dialogs.ConfirmDialog;
import cz.gattserver.common.vaadin.dialogs.WarnDialog;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.services.ArticleService;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.interfaces.ContentNodeTO;
import cz.gattserver.grass.core.interfaces.NodeOverviewTO;
import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.ui.components.DefaultContentOperations;
import cz.gattserver.grass.core.ui.js.JScriptItem;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.core.ui.pages.template.ContentViewerPage;
import cz.gattserver.grass.core.ui.util.UIUtils;
import cz.gattserver.common.server.URLIdentifierUtils;
import cz.gattserver.common.vaadin.HtmlDiv;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

@Route("articles")
public class ArticlesViewerPage extends ContentViewerPage implements HasUrlParameter<String>, HasDynamicTitle {

	private static final long serialVersionUID = 7511698289319715316L;

	@Autowired
	private CoreACLService coreACLService;

	@Autowired
	private ArticleService articleFacade;

	@Autowired
	private PageFactory articlesViewerPageFactory;

	@Resource(name = "articlesEditorPageFactory")
	private PageFactory articlesEditorPageFactory;

	private ArticleTO article;

	@Override
	public String getPageTitle() {
		return article.getContentNode().getName();
	}

	@Override
	public void setParameter(BeforeEvent event, String parameter) {
		URLIdentifierUtils.URLIdentifier identifier = URLIdentifierUtils.parseURLIdentifier(parameter);
		if (identifier == null)
			throw new GrassPageException(404);

		article = articleFacade.getArticleForDetail(identifier.getId());
		if (article == null)
			throw new GrassPageException(404);

		// RESCUE -- tohle by se normálně stát nemělo, ale umožňuje to aspoň
		// vyřešit stav, ve kterém existuje takovýto nezobrazitelný obsah
		if (article.getContentNode() == null) {
			articleFacade.deleteArticle(article.getId(), true);
			UIUtils.redirect(getPageURL(homePageFactory.getPageName()));
		}

		if (!article.getContentNode().isPublicated() && !getUser().isAdmin()
				&& !article.getContentNode().getAuthor().equals(getUser()))
			throw new GrassPageException(403);

		// CSS resources
		for (String css : article.getPluginCSSResources()) {
			// není to úplně nejhezčí řešení, ale dá se tak relativně elegantně
			// obejít problém se závislosí pluginů na úložišti theme apod. a
			// přitom umožnit aby se CSS odkazovali na externí zdroje
			if (!css.toLowerCase().startsWith("http://") || !css.toLowerCase().startsWith("https://"))
				css = getContextPath() + "/VAADIN/" + css;
			loadCSS(css);
		}

		init();

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

				loadJS(jsResourcesArr);
			}
		};
		jsInitDiv.setId(jsInitDivId);
		jsInitDiv.addAttachListener(e -> UI.getCurrent().getPage()
				.executeJs("document.getElementById('" + jsInitDivId + "').$server.initJS();"));
		add(jsInitDiv);
	}

	@Override
	protected ContentNodeTO getContentNodeDTO() {
		return article.getContentNode();
	}

	@Override
	protected void createContent(Div layout) {
		HtmlDiv content = new HtmlDiv(article.getOutputHTML());
		content.setWidthFull();
		content.addClassName("article-content");
		layout.add(content);
	}

	@Override
	protected PageFactory getContentViewerPageFactory() {
		return articlesViewerPageFactory;
	}

	@Override
	protected void createContentOperations(Div operationsListLayout) {
		super.createContentOperations(operationsListLayout);

		// Rychlé úpravy
		if (coreACLService.canModifyContent(article.getContentNode(), getUser())) {
			String url = getPageURL(articlesEditorPageFactory, DefaultContentOperations.EDIT.toString(),
					URLIdentifierUtils.createURLIdentifier(article.getId(), article.getContentNode().getName()));
			String script = "$(\".articles-h-id\").each(" + "function(index){" + "$(this).attr(\"href\",\"" + url
					+ "/\" + $(this).attr(\"href\"));" + "$(this).html(\"[edit]\");" + "}" + ")";
			loadJS(new JScriptItem(script, true));
		}
	}

	@Override
	protected void onDeleteOperation() {
		ConfirmDialog confirmDialog = new ConfirmDialog("Opravdu si přejete smazat tento článek ?", event -> {
			NodeOverviewTO nodeDTO = article.getContentNode().getParent();
			final String nodeURL = getPageURL(nodePageFactory,
					URLIdentifierUtils.createURLIdentifier(nodeDTO.getId(), nodeDTO.getName()));

			// zdařilo se ? Pokud ano, otevři info okno a při
			// potvrzení jdi na kategorii
			try {
				articleFacade.deleteArticle(article.getId(), true);
				UIUtils.redirect(nodeURL);
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
	protected void onEditOperation() {
		UIUtils.redirect(getPageURL(articlesEditorPageFactory, DefaultContentOperations.EDIT.toString(),
				URLIdentifierUtils.createURLIdentifier(article.getId(), article.getContentNode().getName())));
	}

}
