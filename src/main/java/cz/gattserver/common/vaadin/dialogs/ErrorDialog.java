package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.icon.VaadinIcon;

public class ErrorDialog extends MessageDialog {

    public ErrorDialog(String labelCaption) {
        super("Problém", labelCaption, null, VaadinIcon.CLOSE_CIRCLE.create());
    }
}