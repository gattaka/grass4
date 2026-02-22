package cz.gattserver.grass.articles.model;

import com.vaadin.copilot.shaded.checkerframework.checker.units.qual.N;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.SortComparator;

import java.util.Set;
import java.util.SortedSet;

@Setter
@Getter
@NoArgsConstructor
@Entity(name = "ARTICLE_CSS_RESOURCE")
public class ArticleCSSResource {

    @EmbeddedId
    private ArticleCSSResourceId id;

    public ArticleCSSResource(Long articleId, String resource) {
        this.id = new ArticleCSSResourceId(articleId, resource);
    }
}