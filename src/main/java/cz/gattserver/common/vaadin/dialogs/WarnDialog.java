package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

public class WarnDialog extends MessageDialog {

    @Serial
    private static final long serialVersionUID = 5932882030458753323L;

    public WarnDialog(String labelCaption) {
        this(labelCaption, null);
    }

    public WarnDialog(String labelCaption, String details) {
        super("Upozornění", labelCaption, details, VaadinIcon.WARNING.create());
    }
}