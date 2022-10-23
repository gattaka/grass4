package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class CreateButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public CreateButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
		this("Vytvořit", clickListener);
	}

	public CreateButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(caption, new Image(ImageIcon.PLUS_16_ICON.createResource(), "Vytvořit"), clickListener);
	}

}
