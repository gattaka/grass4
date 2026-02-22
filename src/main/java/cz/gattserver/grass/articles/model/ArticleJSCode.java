package cz.gattserver.grass.articles.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "ARTICLE_JS_CODE")
public class ArticleJSCode implements ExecutedInOrder, Comparable<ArticleJSCode> {

    /**
     * DB identifikátor
     */
    @EmbeddedId
    private ArticleJSCodeId id;

    /**
     * Obsah skriptu
     */
    @EqualsAndHashCode.Include
    @Column(columnDefinition = "TEXT")
    private String content;

    /**
     * Pořadí při nahrávání
     */
    private Integer executionOrder = 0;

    public ArticleJSCode(Long articleId, String resource, Integer executionOrder) {
        this.id = new ArticleJSCodeId(articleId, resource);
        this.executionOrder = executionOrder;
    }

    @Override
    public int compareTo(ArticleJSCode resource) {
        return this.getExecutionOrder().compareTo(resource.getExecutionOrder());
    }
}