package cz.gattserver.grass.language.web;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;


@Component("languagePageFactory")
public class LanguagePageFactory extends AbstractPageFactory {

	public LanguagePageFactory() {
		super("language");
	}

}
