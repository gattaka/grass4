package cz.gattserver.grass.recipes.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import cz.gattserver.grass.recipes.model.domain.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeRepositoryCustom {

}
