package cz.gattserver.grass.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cz.gattserver.grass.recipes.facades.RecipesService;
import cz.gattserver.grass.recipes.model.dto.RecipeDTO;
import cz.gattserver.grass.recipes.model.dto.RecipeOverviewTO;

@Controller
@RequestMapping("/ws/recipes")
public class RecipesResource {

	@Autowired
	private RecipesService recipesFacade;

	@RequestMapping("/list")
	public ResponseEntity<List<RecipeOverviewTO>> list(@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "pageSize", required = true) int pageSize,
			@RequestParam(value = "filter", required = false) String filter) {
		int count = recipesFacade.getRecipesCount(filter);
		// startIndex nesmí být víc než je počet, endIndex může být s tím si JPA
		// poradí a sníží ho
		if (page * pageSize > count)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>(recipesFacade.getRecipes(filter, page * pageSize, page), HttpStatus.OK);
	}

	@RequestMapping("/count")
	public ResponseEntity<Integer> count(@RequestParam(value = "filter", required = false) String filter) {
		return new ResponseEntity<>(recipesFacade.getRecipesCount(filter), HttpStatus.OK);
	}

	@RequestMapping("/recipe")
	public @ResponseBody RecipeDTO recipe(@RequestParam(value = "id", required = true) Long id) {
		return recipesFacade.getRecipeById(id);
	}

}
