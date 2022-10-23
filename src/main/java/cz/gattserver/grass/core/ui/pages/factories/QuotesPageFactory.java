package cz.gattserver.grass.core.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;

@Component("quotesPageFactory")
public class QuotesPageFactory extends AbstractPageFactory {

	public QuotesPageFactory() {
		super("quotes");
	}

}
