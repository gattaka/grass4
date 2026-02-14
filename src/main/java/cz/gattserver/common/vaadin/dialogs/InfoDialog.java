package cz.gattserver.common.vaadin.dialogs;


import com.vaadin.flow.component.icon.VaadinIcon;

public class InfoDialog extends MessageDialog {

    public InfoDialog(String labelCaption) {
        super("Info", labelCaption,null, VaadinIcon.INFO_CIRCLE_O.create());
    }
}