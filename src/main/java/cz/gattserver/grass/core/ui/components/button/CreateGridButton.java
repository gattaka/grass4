package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import cz.gattserver.common.vaadin.ImageIcon;

public class CreateGridButton extends Button {

    private static final long serialVersionUID = -5924239277930098183L;

    public CreateGridButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(caption, clickListener);
        setIcon(ImageIcon.PLUS_16_ICON.createImage("Vytvořit"));
    }
}