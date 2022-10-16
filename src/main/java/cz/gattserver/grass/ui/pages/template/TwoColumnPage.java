package cz.gattserver.grass.ui.pages.template;

import com.vaadin.flow.component.html.Div;

public abstract class TwoColumnPage extends BasePage {

	private static final long serialVersionUID = -6878321656448036198L;

	@Override
	protected void createCenterElements(Div layout) {
		Div leftContentLayout = new Div();
		leftContentLayout.setId("left-content");
		layout.add(leftContentLayout);
		createLeftColumnContent(leftContentLayout);

		Div rightContentLayout = new Div();
		rightContentLayout.setId("right-content");
		layout.add(rightContentLayout);
		createRightColumnContent(rightContentLayout);
	}

	/**
	 * Obsah levé části
	 * 
	 * @return layout
	 */
	protected abstract void createLeftColumnContent(Div leftContentLayout);

	/**
	 * Obsah pravé části
	 * 
	 * @return layout
	 */
	protected abstract void createRightColumnContent(Div rightContentLayout);

}
