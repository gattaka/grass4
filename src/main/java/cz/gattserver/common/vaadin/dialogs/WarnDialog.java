package cz.gattserver.common.vaadin.dialogs;

import com.vaadin.flow.component.icon.VaadinIcon;

public class WarnDialog extends MessageDialog {

    private static final long serialVersionUID = -4793025663820815400L;

    public WarnDialog(String labelCaption) {
        this(labelCaption, null);
    }

    public WarnDialog(String labelCaption, String details) {
        super(labelCaption, details, VaadinIcon.WARNING.create());
    }
}