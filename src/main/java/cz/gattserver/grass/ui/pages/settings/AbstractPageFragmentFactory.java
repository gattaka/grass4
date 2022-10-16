package cz.gattserver.grass.ui.pages.settings;

import com.vaadin.flow.component.html.Div;

import cz.gattserver.web.common.spring.SpringContextHelper;

public abstract class AbstractPageFragmentFactory {

	public AbstractPageFragmentFactory() {
		SpringContextHelper.inject(this);
	}

	public abstract void createFragment(Div layout);

}
