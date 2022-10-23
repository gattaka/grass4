package cz.gattserver.grass.core.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;

@Component(value = "noServicePageFactory")
public class NoServicePageFactory extends AbstractPageFactory {

	public NoServicePageFactory() {
		super("noservice");
	}

}
