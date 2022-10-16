package cz.gattserver.grass.ui.pages.template;

import com.vaadin.flow.component.html.Div;

public abstract class OneColumnPage extends BasePage {

	private static final long serialVersionUID = 5541555440277025949L;

	@Override
	protected void createCenterElements(Div layout) {
		Div contentLayout = new Div();
		contentLayout.setId("center-content");
		layout.add(contentLayout);
		createColumnContent(contentLayout);
	}

	/**
	 * Obsah sloupce
	 * 
	 * @return layout
	 */
	protected abstract void createColumnContent(Div layout);

}
