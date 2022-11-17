package cz.gattserver.grass.fm.web.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("fmPageFactory")
public class FMPageFactory extends AbstractPageFactory {

	public FMPageFactory() {
		super("fm");
	}

}
