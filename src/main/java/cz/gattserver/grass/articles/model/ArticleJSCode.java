package cz.gattserver.grass.articles.model;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.C;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity(name = "ARTICLE_JS_CODE")
public class ArticleJSCode implements ExecutedInOrder, Comparable<ArticleJSCode> {

    /**
     * DB identifikátor
     */
    @EmbeddedId
    private ArticleJSCodeId id;

    /**
     * Pořadí při nahrávání
     */
    @Column(name = "EXECUTION_ORDER")
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