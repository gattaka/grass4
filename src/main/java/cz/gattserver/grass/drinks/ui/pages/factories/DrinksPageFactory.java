package cz.gattserver.grass.drinks.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("drinksPageFactory")
public class DrinksPageFactory extends AbstractPageFactory {

	public DrinksPageFactory() {
		super("drinks");
	}
}
