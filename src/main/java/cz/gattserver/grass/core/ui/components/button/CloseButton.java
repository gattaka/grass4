package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class CloseButton extends ImageButton {

	private static final long serialVersionUID = -9054113192020716390L;

	public CloseButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
		this("Zavřít", clickListener);
	}

	public CloseButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(caption, new Image(ImageIcon.BLOCK_16_ICON.createResource(), "Zavřít"), clickListener);
	}

}
