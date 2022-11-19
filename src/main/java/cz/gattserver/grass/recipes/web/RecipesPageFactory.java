package cz.gattserver.grass.recipes.web;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;


@Component("recipesPageFactory")
public class RecipesPageFactory extends AbstractPageFactory {

	public RecipesPageFactory() {
		super("recipes");
	}
}
