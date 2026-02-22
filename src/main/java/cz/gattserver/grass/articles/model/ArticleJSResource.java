package cz.gattserver.grass.articles.model;

import jakarta.persistence.*;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "ARTICLE_JS_RESOURCE")
public class ArticleJSResource implements ExecutedInOrder, Comparable<ArticleJSResource> {

    /**
     * DB identifikátor
     */
    @EmbeddedId
    private ArticleJSResourceId id;

    /**
     * Pořadí při nahrávání
     */
    @Column(name = "EXECUTION_ORDER")
    private Integer executionOrder = 0;

    public ArticleJSResource(Long articleId, String resource, Integer executionOrder) {
        this.id = new ArticleJSResourceId(articleId, resource);
        this.executionOrder = executionOrder;
    }

    @Override
    public int compareTo(ArticleJSResource resource) {
        return this.getExecutionOrder().compareTo(resource.getExecutionOrder());
    }
}