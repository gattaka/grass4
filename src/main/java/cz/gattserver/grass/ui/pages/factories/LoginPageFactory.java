package cz.gattserver.grass.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.ui.pages.factories.template.AbstractPageFactory;

@Component("loginPageFactory")
public class LoginPageFactory extends AbstractPageFactory {

	public LoginPageFactory() {
		// kvůli Spring security se tohle nesmí jmenovat login, musí tam být
		// něco jiného, aby se nechytil filtr -- ten rozhodí Vaadin JSON
		// komunikaci a stránka nenajede
		super("loginpage");
	}

}
