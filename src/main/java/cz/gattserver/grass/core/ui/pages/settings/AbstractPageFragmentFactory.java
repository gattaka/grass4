package cz.gattserver.grass.core.ui.pages.settings;

import com.vaadin.flow.component.html.Div;

import cz.gattserver.common.spring.SpringContextHelper;

public abstract class AbstractPageFragmentFactory {

	public AbstractPageFragmentFactory() {
		SpringContextHelper.inject(this);
	}

	public abstract void createFragment(Div layout);

}
