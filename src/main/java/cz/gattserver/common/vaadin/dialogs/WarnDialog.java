package cz.gattserver.common.vaadin.dialogs;

import cz.gattserver.common.vaadin.ImageIcon;

public class WarnDialog extends MessageDialog {

	private static final long serialVersionUID = -4793025663820815400L;

	public WarnDialog(String labelCaption) {
		this(labelCaption, null);
	}

	public WarnDialog(String labelCaption, String details) {
		super(labelCaption, details, ImageIcon.WARNING_16_ICON.createImage());
	}
}