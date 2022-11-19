package cz.gattserver.grass.recipes.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.recipes.model.domain.Recipe;
import cz.gattserver.grass.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass.recipes.model.dto.RecipeOverviewTO;

/**
 * <b>Mapper pro různé typy.</b>
 * 
 * <p>
 * Je potřeba aby byl volán na objektech s aktivními proxy objekty. To znamená,
 * že před tímto mapperem nedošlo k uzavření session, ve které byl původní
 * objekt pořízen.
 * </p>
 * 
 * <p>
 * Mapper využívá proxy objekty umístěné v atributech předávaných entit. Během
 * mapování tak může docházet k dotazům na DB, které produkují tyto proxy
 * objekty a které se bez původní session mapovaného objektu neobejdou.
 * </p>
 * 
 * @author gatt
 * 
 */
@Component("recipeMapper")
public class Mapper {

	/**
	 * Převede {@link Recipe} na {@link RecipeDTO}
	 * 
	 * @param e
	 * @return
	 */
	public RecipeDTO mapRecipe(Recipe e) {
		if (e == null)
			return null;

		RecipeDTO recipeDTO = new RecipeDTO();

		recipeDTO.setId(e.getId());
		recipeDTO.setName(e.getName());
		recipeDTO.setDescription(e.getDescription());

		return recipeDTO;
	}

	/**
	 * Převede list {@link Recipe} na list {@link RecipeDTO}
	 * 
	 * @param recipes
	 * @return
	 */
	public List<RecipeOverviewTO> mapRecipes(Collection<Recipe> recipes) {
		if (recipes == null)
			return null;

		List<RecipeOverviewTO> recipeDTOs = new ArrayList<RecipeOverviewTO>();
		for (Recipe recipe : recipes) {
			recipeDTOs.add(new RecipeOverviewTO(recipe.getId(), recipe.getName()));
		}
		return recipeDTOs;
	}
}