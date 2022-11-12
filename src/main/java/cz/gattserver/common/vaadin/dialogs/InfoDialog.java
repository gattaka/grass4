package cz.gattserver.common.vaadin.dialogs;


import cz.gattserver.common.vaadin.ImageIcon;

public class InfoDialog extends MessageDialog {

	private static final long serialVersionUID = -4793025663820815400L;

	public InfoDialog(String labelCaption) {
		super("Info", labelCaption, ImageIcon.INFO_16_ICON.createResource());
	}

}
