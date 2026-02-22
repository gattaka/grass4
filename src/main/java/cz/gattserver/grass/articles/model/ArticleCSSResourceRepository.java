package cz.gattserver.grass.articles.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ArticleCSSResourceRepository extends JpaRepository<ArticleCSSResource, ArticleCSSResourceId> {

    @Query("select r.id.resource from ARTICLE_CSS_RESOURCE r where r.id.articleId = ?1")
    Set<String> findByArticleId(Long articleId);

    @Modifying
    @Query("delete ARTICLE_CSS_RESOURCE where id.articleId = ?1 and id.resource in ?2")
    void deleteCSSResources(Long articleId, Set<String> resources);

}