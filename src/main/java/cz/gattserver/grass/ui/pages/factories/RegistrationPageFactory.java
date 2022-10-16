package cz.gattserver.grass.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.ui.pages.factories.template.AbstractPageFactory;

@Component("registrationPageFactory")
public class RegistrationPageFactory extends AbstractPageFactory {

	public RegistrationPageFactory() {
		super("registration");
	}

}
