package cz.gattserver.grass.articles.model;

import cz.gattserver.grass.articles.editor.parser.interfaces.ArticleDraftOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTOverviewTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryRESTTO;
import cz.gattserver.grass.pg.interfaces.PhotogalleryTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepositoryCustom {

    @Query(value = "select a from ARTICLE a join a.contentNode c where c.draft = true and (?2 = true or c.author.id = ?1) order by c.creationDate desc")
    List<ArticleDraftOverviewTO> findDraftsForUser(Long userId, boolean admin);

    @Query(value = "select a from ARTICLE a join a.contentNode c where c.draft = false and (c.publicated = true or (?2 = true or c.author.id = ?1)) order by c.creationDate desc")
    List<Article> findAllForSearch(Long userId, boolean admin);

    @Query("select a from ARTICLE a where a.attachmentsDirId is not null")
    List<Article> findWithAttachments();

}