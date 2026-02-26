package cz.gattserver.grass.recipes.interfaces;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeOverviewTO {

    /**
     * DB identifikátor
     */
    private Long id;

	/**
	 * Název receptu
	 */
	private String name;

}