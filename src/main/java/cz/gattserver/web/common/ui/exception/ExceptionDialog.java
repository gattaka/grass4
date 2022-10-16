package cz.gattserver.web.common.ui.exception;

import org.apache.commons.lang3.exception.ExceptionUtils;

import cz.gattserver.web.common.ui.ImageIcon;
import cz.gattserver.web.common.ui.dialog.MessageDialog;

public class ExceptionDialog extends MessageDialog {

	private static final long serialVersionUID = -2077736292967107272L;

	public ExceptionDialog(Throwable throwable) {
		super("Neočekávaná systémová chyba", ExceptionUtils.getStackTrace(throwable),
				ImageIcon.DELETE_16_ICON.createResource());
		layout.addClassName("error-layout");
		detailsArea.addClassName("error-text-field");
		detailsArea.setHeight("500px");
		setWidth("1200px");
	}

}
