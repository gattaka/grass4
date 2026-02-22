package cz.gattserver.grass.articles.model;

import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleTO;

import java.util.List;

public interface ArticleRepositoryCustom {

    ArticleTO findByForDetailId(Long id, Long userId, boolean isAdmin);

    List<ArticleDraftOverviewTO> findDraftsForUser(Long userId);
}