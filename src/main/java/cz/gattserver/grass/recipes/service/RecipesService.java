package cz.gattserver.grass.recipes.service;

import java.util.List;

import cz.gattserver.grass.recipes.interfaces.RecipeTO;
import cz.gattserver.grass.recipes.interfaces.RecipeOverviewTO;

public interface RecipesService {

	/**
	 * Získá počet receptů v DB
	 *
	 * @param filter název receptu (s *)
	 */
	int getRecipesCount(String filter);

	/**
	 * Získá všechny recepty
	 *
	 * @param filter filtr
	 * @param offset stránkování
	 * @param limit  limit
	 */
	List<RecipeOverviewTO> getRecipes(String filter, int offset, int limit);

	/**
	 * Získá recept dle id
	 */
	RecipeTO getRecipeById(Long id);

	/**
	 * Založí/uprav nový recept
	 */
	Long saveRecipe(String name, String desc, Long id);

	Long saveRecipe(String name, String desc);

	/**
	 * Odstraní recept
	 */
	void deleteRecipe(Long id);

	/**
	 * Převede každý "< br/ >" nebo "< br >" v textu na EOL znak
	 */
	String breaklineToEol(String text);

	/**
	 * Převede každý EOL znak v textu na "< br/ >"
	 */
	String eolToBreakline(String text);

}
