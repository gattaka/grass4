package cz.gattserver.grass.recipes.model;

import java.util.List;

public interface RecipeRepositoryCustom {

	long count(String filter);

	List<Recipe> fetch(String filter, int offset, int limit);
}
