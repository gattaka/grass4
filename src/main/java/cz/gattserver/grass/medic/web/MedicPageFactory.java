package cz.gattserver.grass.medic.web;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;

@Component("medicPageFactory")
public class MedicPageFactory extends AbstractPageFactory {

	public MedicPageFactory() {
		super("medic");
	}

}
