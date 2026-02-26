package cz.gattserver.grass.recipes.interfaces;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RecipeTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3778342407025065395L;

    /**
     * DB identifikátor
     */
    @EqualsAndHashCode.Include
    private Long id;

    /**
	 * Název receptu
	 */
	private String name;

	/**
	 * Popis receptu
	 */
	private String description;

    @QueryProjection
    public RecipeTO(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}