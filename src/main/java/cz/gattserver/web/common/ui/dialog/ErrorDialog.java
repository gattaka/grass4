package cz.gattserver.web.common.ui.dialog;

import cz.gattserver.web.common.ui.ImageIcon;

public class ErrorDialog extends MessageDialog {

	private static final long serialVersionUID = -4793025663820815400L;

	public ErrorDialog(String labelCaption) {
		super("Problém", labelCaption, ImageIcon.DELETE_16_ICON.createResource());
	}

}
