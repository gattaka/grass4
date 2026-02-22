package cz.gattserver.grass.articles.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface ArticleJSCodeRepository extends JpaRepository<ArticleJSCode, ArticleJSCodeId> {

    @Query("select c.id.code from ARTICLE_JS_CODE c where c.id.articleId = ?1")
    Set<String> findByArticleId(Long articleId);

    @Modifying
    @Query("delete ARTICLE_JS_CODE where id.articleId = ?1 and id.code in ?2")
    void deleteJSCodes(Long articleId, Set<String> code);

}