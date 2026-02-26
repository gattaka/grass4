package cz.gattserver.grass.recipes.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -3778342407025065395L;

    /**
	 * Název receptu
	 */
	private String name;

	/**
	 * Popis receptu
	 */
	private String description;

	/**
	 * DB identifikátor
	 */
	private Long id;
}