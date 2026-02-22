package cz.gattserver.grass.articles.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long>, ArticleRepositoryCustom {

    @Query("select a.id from ARTICLE a")
    List<Long> findAllIds();

    @Modifying
    @Query("update ARTICLE set outputHTML = ?2, searchableOutput = ?3 where id = ?1")
    void updateOutputs(Long id, String outputHTML, String searchableOutput);

    @Query("select a.id from ARTICLE a where a.attachmentsDirId is not null")
    List<Long> findIdsWithAttachments();

    @Modifying
    @Query("update ARTICLE set contentNodeId = ?2 where id = ?1")
    void updateContentNodeId(Long id, Long contentNodeId);
}