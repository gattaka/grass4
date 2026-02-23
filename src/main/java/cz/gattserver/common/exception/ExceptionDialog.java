package cz.gattserver.common.exception;

import com.vaadin.flow.component.icon.VaadinIcon;
import cz.gattserver.common.vaadin.dialogs.MessageDialog;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.Serial;

public class ExceptionDialog extends MessageDialog {

    @Serial
    private static final long serialVersionUID = 3429761579021523767L;

    public ExceptionDialog(Throwable throwable) {
        super("Chyba", "Neočekávaná systémová chyba", ExceptionUtils.getStackTrace(throwable),
                VaadinIcon.CLOSE_CIRCLE.create());
        layout.addClassName("error-layout");
        detailsArea.addClassName("error-text-field");
        detailsArea.setHeight("500px");
        setWidth("1200px");
    }
}