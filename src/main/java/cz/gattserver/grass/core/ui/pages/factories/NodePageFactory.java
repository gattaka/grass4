package cz.gattserver.grass.core.ui.pages.factories;

import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;

@Component("nodePageFactory")
public class NodePageFactory extends AbstractPageFactory {

	public NodePageFactory() {
		super("category");
	}

}
