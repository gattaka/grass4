package cz.gattserver.grass.core.ui.components;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;

import cz.gattserver.grass.core.ui.components.button.SaveButton;

public class SaveCloseLayout extends OperationsLayout {

	private static final long serialVersionUID = 3909022460514320026L;

	protected SaveButton saveButton;

	public SaveCloseLayout(ComponentEventListener<ClickEvent<Button>> saveClickListener,
			ComponentEventListener<ClickEvent<Button>> closeClickListener) {
		super(closeClickListener);
		setJustifyContentMode(JustifyContentMode.BETWEEN);
		setSpacing(false);
		setWidthFull();

		saveButton = new SaveButton(saveClickListener);
		add(saveButton);
	}

	public SaveButton getSaveButton() {
		return saveButton;
	}

}
