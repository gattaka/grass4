package cz.gattserver.grass.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class ModifyButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public ModifyButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
		this("Upravit", clickListener);
	}

	public ModifyButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(caption, new Image(ImageIcon.PENCIL_16_ICON.createResource(), "Upravit"), clickListener);
	}

}
