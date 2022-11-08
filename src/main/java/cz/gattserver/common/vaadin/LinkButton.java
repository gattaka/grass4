package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;

public class LinkButton extends Button {
	private static final long serialVersionUID = 3709000883176555567L;

	public LinkButton(String text, ComponentEventListener<ClickEvent<Button>> clickListener) {
		super(text, clickListener);
		addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		getStyle().set("cursor", "pointer");
		addClassName("button-link");
	}

}
