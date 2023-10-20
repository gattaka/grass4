package cz.gattserver.grass.core.ui.pages.template;

import jakarta.annotation.Resource;

import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.grass.core.services.QuotesService;
import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;

public abstract class BasePage extends MenuPage {

	private static final long serialVersionUID = -2685924539988484100L;

	@Autowired
	protected QuotesService quotesFacade;

	@Resource(name = "quotesPageFactory")
	protected PageFactory quotesPageFactory;

	@Override
	protected void createQuotes(Div layout) {
		Anchor quotes = new Anchor(getPageURL(quotesPageFactory), chooseQuote());
		layout.add(quotes);
	}

	private String chooseQuote() {
		String quote = quotesFacade.getRandomQuote();
		if (quote == null)
			throw new GrassPageException(500);
		return quote;
	}

}
