package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

public class InfoDialog extends MessageDialog {

    @Serial
    private static final long serialVersionUID = 6133541851138881616L;

    public InfoDialog(String labelCaption) {
        super("Info", labelCaption,null, VaadinIcon.INFO_CIRCLE_O.create());
    }
}