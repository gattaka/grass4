package cz.gattserver.grass.articles.services;

import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleRESTTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;
import cz.gattserver.grass.articles.model.Article;

import java.util.List;

public interface ArticlesMapperService {

    /**
     * Převede {@link Article} na {@link ArticleTO}
     */
    ArticleTO mapArticleForDetail(Article article);

    /**
     * Převede {@link Article} na {@link ArticleRESTTO}
     */
    ArticleRESTTO mapArticleForREST(Article article);

}