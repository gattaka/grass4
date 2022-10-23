package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;

import cz.gattserver.web.common.ui.ImageIcon;

public class ImageButton extends Button {

	private static final long serialVersionUID = 4204958919924333786L;

	public ImageButton(String caption, ImageIcon icon) {
		this(caption, new Image(icon.createResource(), caption), null);
	}

	public ImageButton(String caption, Image img) {
		this(caption, img, null);
	}

	public ImageButton(String caption, ImageIcon icon, ComponentEventListener<ClickEvent<Button>> clickListener) {
		this(caption, new Image(icon.createResource(), caption), clickListener);
	}

	public ImageButton(String caption, Image img, ComponentEventListener<ClickEvent<Button>> clickListener) {
		if (caption != null) {
			setText(caption);
			setTooltip(caption);
		}
		if (img != null) {
			setIcon(img);
			if (caption == null)
				img.addClassName("img-only-button");
		}
		if (clickListener != null)
			addClickListener(clickListener);
	}

	public ImageButton setTooltip(String value) {
		getElement().setProperty("title", value);
		return this;
	}

	public ImageButton clearText() {
		setText(null);
		((Image) getIcon()).addClassName("img-only-button");
		return this;
	}

}
