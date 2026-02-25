package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

public class ErrorDialog extends MessageDialog {

    @Serial
    private static final long serialVersionUID = 4174543158204594614L;

    public ErrorDialog(String labelCaption) {
        super("Problém", labelCaption, null, VaadinIcon.CLOSE_CIRCLE.create());
    }
}