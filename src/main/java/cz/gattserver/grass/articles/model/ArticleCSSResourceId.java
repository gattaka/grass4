package cz.gattserver.grass.articles.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class ArticleCSSResourceId {

    @Column(name = "ARTICLE_ID")
    private Long articleId;

	@Column(name = "RESOURCE")
	private String resource;

}