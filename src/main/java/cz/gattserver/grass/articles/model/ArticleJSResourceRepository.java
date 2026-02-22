package cz.gattserver.grass.articles.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ArticleJSResourceRepository extends JpaRepository<ArticleJSResource, ArticleJSResource> {

    @Query("select r.id.resource from ARTICLE_JS_RESOURCE r where r.id.articleId = ?1")
    Set<String> findJSResourcesByArticleId(Long articleId);

    @Modifying
    @Query("delete ARTICLE_JS_RESOURCE where id.articleId = ?1 and id.resource in ?2")
    void deleteJSResources(Long articleId, Set<String> resources);

}