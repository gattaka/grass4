package cz.gattserver.grass.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class SaveButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public SaveButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
		this("Uložit", clickListener);
	}

	public SaveButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(caption, new Image(ImageIcon.TICK_16_ICON.createResource(), "Uložit"), clickListener);
	}

}
