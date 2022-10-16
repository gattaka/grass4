package cz.gattserver.grass.ui.pages.template;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;

import cz.gattserver.grass.exception.GrassPageException;
import cz.gattserver.grass.services.QuotesService;
import cz.gattserver.grass.ui.pages.factories.template.PageFactory;

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
