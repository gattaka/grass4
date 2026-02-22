package cz.gattserver.grass.articles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ArticleJSResourceId {

    @Column(name = "ARTICLE_ID")
    private Long articleId;

	@Column(name = "RESOURCE")
	private String resource;
}