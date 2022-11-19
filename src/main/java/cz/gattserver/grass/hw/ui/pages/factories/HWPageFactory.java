package cz.gattserver.grass.hw.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;


@Component("hwPageFactory")
public class HWPageFactory extends AbstractPageFactory {

	public HWPageFactory() {
		super("hw");
	}

}
