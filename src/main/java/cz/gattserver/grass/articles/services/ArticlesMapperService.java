package cz.gattserver.grass.articles.services;

import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleRESTTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.model.domain.Article;

import java.util.List;

public interface ArticlesMapperService {

	/**
	 * Převede {@link Article} na {@link ArticleTO}
	 */
	public ArticleTO mapArticleForDetail(Article article);

	/**
	 * Převede {@link Article} na {@link ArticleRESTTO}
	 */
	public ArticleRESTTO mapArticleForREST(Article article);

	/**
	 * Převede kolekci {@link Article} na kolekci {@link ArticleDraftOverviewTO}
	 * určenou pro menu výběru rozpracovaného článku
	 * 
	 * @param articles
	 *            vstupní kolekce entit {@link Article}
	 * @return
	 */
	public List<ArticleDraftOverviewTO> mapArticlesForDraftOverview(List<Article> articles);

	/**
	 * Převede {@link Article} na {@link ArticleDraftOverviewTO} určený pro menu
	 * výběru rozpracovaného článku
	 * 
	 * @param article
	 *            vstupní {@link Article}
	 * @return
	 */
	public ArticleDraftOverviewTO mapArticleForDraftOverview(Article article);

}
