package cz.gattserver.common.vaadin.dialogs;

import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.dialogs.MessageDialog;

public class ErrorDialog extends MessageDialog {

	private static final long serialVersionUID = -4793025663820815400L;

	public ErrorDialog(String labelCaption) {
		super("Problém", labelCaption, ImageIcon.DELETE_16_ICON.createResource());
	}

}
