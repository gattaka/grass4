package cz.gattserver.grass.articles.model.domain;

import jakarta.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "ARTICLE_JS_RESOURCE")
public class ArticleJSResource implements ExecutedInOrder, Comparable<ArticleJSResource> {

    /**
     * DB identifikátor
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Jméno skriptu
     */
    @EqualsAndHashCode.Include
    private String name;

    /**
     * Pořadí při nahrávání
     */
    private Integer executionOrder = 0;

    @Override
    public int compareTo(ArticleJSResource resource) {
        return this.getExecutionOrder().compareTo(resource.getExecutionOrder());
    }
}