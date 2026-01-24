package cz.gattserver.common.vaadin.dialogs;


import com.vaadin.flow.component.icon.VaadinIcon;

public class InfoDialog extends MessageDialog {

    private static final long serialVersionUID = -4793025663820815400L;

    public InfoDialog(String labelCaption) {
        super("Info", labelCaption,null, VaadinIcon.INFO_CIRCLE_O.create());
    }
}