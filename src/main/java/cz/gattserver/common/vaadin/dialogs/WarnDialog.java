package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.icon.VaadinIcon;

public class WarnDialog extends MessageDialog {

    public WarnDialog(String labelCaption) {
        this(labelCaption, null);
    }

    public WarnDialog(String labelCaption, String details) {
        super("Upozornění", labelCaption, details, VaadinIcon.WARNING.create());
    }
}