package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import cz.gattserver.common.vaadin.ImageIcon;

public class SaveButton extends ImageButton {

    private static final long serialVersionUID = -9054113192020716390L;

    public SaveButton(ComponentEventListener<ClickEvent<Button>> clickListener) {
        this("Uložit", clickListener);
    }

    public SaveButton(String caption, ComponentEventListener<ClickEvent<Button>> clickListener) {
        super(caption, ImageIcon.TICK_16_ICON.createImage("Uložit"), clickListener);
    }
}