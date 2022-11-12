package cz.gattserver.common.exception;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.common.vaadin.dialogs.MessageDialog;
import org.apache.commons.lang3.exception.ExceptionUtils;

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
