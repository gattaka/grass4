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
@Entity(name = "ARTICLE_JS_CODE")
public class ArticleJSCode implements ExecutedInOrder, Comparable<ArticleJSCode> {

	/**
	 * DB identifikátor
	 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	@Override
	public int compareTo(ArticleJSCode resource) {
		return this.getExecutionOrder().compareTo(resource.getExecutionOrder());
	}
}