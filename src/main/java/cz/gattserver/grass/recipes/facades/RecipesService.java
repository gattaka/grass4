package cz.gattserver.grass.recipes.facades;

import java.util.List;

import cz.gattserver.grass.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass.recipes.model.dto.RecipeOverviewTO;

public interface RecipesService {

	/**
	 * Získá počet receptů v DB
	 *
	 * @param filter název receptu (s *)
	 */
	public int getRecipesCount(String filter);

	/**
	 * Získá všechny recepty
	 *
	 * @param filter filtr
	 * @param offset stránkování
	 * @param limit limit
	 */
	public List<RecipeOverviewTO> getRecipes(String filter, int offset, int limit);

	/**
	 * Získá recept dle id
	 */
	public RecipeDTO getRecipeById(Long id);

	/**
	 * Založí/uprav nový recept
	 */
	public Long saveRecipe(String name, String desc, Long id);

	public Long saveRecipe(String name, String desc);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	public String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	public String eolToBreakline(String text);

}
