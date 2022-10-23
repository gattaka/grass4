package cz.gattserver.grass.campgames.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;

@Component("campgamesPageFactory")
public class CampgamesPageFactory extends AbstractPageFactory {

	public CampgamesPageFactory() {
		super("campgames");
	}

}
