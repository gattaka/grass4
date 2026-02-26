package cz.gattserver.grass.recipes.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepositoryCustom {

}
