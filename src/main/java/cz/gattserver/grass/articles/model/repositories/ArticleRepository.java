package cz.gattserver.grass.articles.model.repositories;

import cz.gattserver.grass.articles.model.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

	@Query(value = "select a from ARTICLE a join a.contentNode c where c.draft = true and (?2 = true or c.author.id = ?1) order by c.creationDate desc")
	List<Article> findDraftsForUser(Long userId, boolean admin);

	@Query(value = "select a from ARTICLE a join a.contentNode c where c.draft = false and (c.publicated = true or (?2 = true or c.author.id = ?1)) order by c.creationDate desc")
	List<Article> findAllForSearch(Long userId, boolean admin);

    @Query("select a from ARTICLE a where a.attachmentsDirId is not null")
    List<Article> findWithAttachments();

    @Modifying
    @Query("update ARTICLE a set a.attachmentsDirId = null where a.id = ?1")
    void clearAttachmentsDirId(Long id);
}
