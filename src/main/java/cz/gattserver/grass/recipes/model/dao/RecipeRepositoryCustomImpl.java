package cz.gattserver.grass.recipes.model.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import org.springframework.stereotype.Repository;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;

import cz.gattserver.grass.recipes.model.domain.QRecipe;
import cz.gattserver.grass.recipes.model.domain.Recipe;

@Repository
public class RecipeRepositoryCustomImpl implements RecipeRepositoryCustom {

	@PersistenceContext
	private EntityManager entityManager;

	private Predicate createPredicate(String name) {
		QRecipe r = QRecipe.recipe;
		PredicateBuilder builder = new PredicateBuilder();
		if (name != null)
			builder.iLike(r.name, name);
		return builder.getBuilder();
	}

	@Override
	public int count(String name) {
		JPAQuery<Recipe> query = new JPAQuery<>(entityManager);
		QRecipe r = QRecipe.recipe;
		return (int) query.from(r).where(createPredicate(name)).fetchCount();
	}

	@Override
	public List<Recipe> fetch(String name, int offset, int limit) {
		JPAQuery<Recipe> query = new JPAQuery<>(entityManager);
		QRecipe r = QRecipe.recipe;
		return query.from(r).where(createPredicate(name)).orderBy(r.name.desc()).fetch();
	}

}
