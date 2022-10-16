package cz.gattserver.grass.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import cz.gattserver.grass.ui.components.button.CloseButton;
import cz.gattserver.grass.ui.util.UIUtils;

public class OperationsLayout extends HorizontalLayout {

	private static final long serialVersionUID = -7665980667994173144L;

	private HorizontalLayout buttonLayout;

	protected CloseButton closeButton;

	public OperationsLayout(ComponentEventListener<ClickEvent<Button>> closeClickListener) {
		setSpacing(false);
		setJustifyContentMode(JustifyContentMode.BETWEEN);
		addClassName(UIUtils.TOP_MARGIN_CSS_CLASS);

		buttonLayout = new HorizontalLayout();
		buttonLayout.setPadding(false);
		super.add(buttonLayout);

		closeButton = new CloseButton(closeClickListener);
		super.add(closeButton);
	}

	public CloseButton getCloseButton() {
		return closeButton;
	}

	@Override
	public void add(Component... components) {
		buttonLayout.add(components);
	}
}
