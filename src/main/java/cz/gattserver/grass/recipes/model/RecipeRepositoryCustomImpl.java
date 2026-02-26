package cz.gattserver.grass.recipes.model;

import java.util.List;

import cz.gattserver.grass.recipes.interfaces.RecipeTO;

import cz.gattserver.grass.core.model.util.PredicateBuilder;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import com.querydsl.core.types.Predicate;

public class RecipeRepositoryCustomImpl extends QuerydslRepositorySupport implements RecipeRepositoryCustom {

    private final QRecipe r = QRecipe.recipe;

    public RecipeRepositoryCustomImpl() {
        super(RecipeTO.class);
    }

    private Predicate createPredicate(String name) {
        QRecipe r = QRecipe.recipe;
        PredicateBuilder builder = new PredicateBuilder();
        if (name != null) builder.iLike(r.name, name);
        return builder.getBuilder();
    }

    @Override
    public long count(String name) {
        return from(r).where(createPredicate(name)).stream().count();
    }

    @Override
    public List<Recipe> fetch(String name, int offset, int limit) {
        return from(r).where(createPredicate(name)).orderBy(r.name.desc()).fetch();
    }
}