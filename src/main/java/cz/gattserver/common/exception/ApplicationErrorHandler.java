package cz.gattserver.common.exception;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ErrorEvent;
import com.vaadin.flow.server.ErrorHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

@Slf4j
public class ApplicationErrorHandler implements ErrorHandler {

    @Serial
    private static final long serialVersionUID = 5130295169350043481L;

    @Override
    public void error(ErrorEvent event) {
        error(event.getThrowable());
    }

    public void error(Throwable throwable) {
        String log = new SystemException("V aplikaci došlo k neočekávané chybě", throwable).toString();
        ApplicationErrorHandler.log.error(log);
        if (UI.getCurrent() != null) UI.getCurrent().access(() -> new ExceptionDialog(throwable).open());
    }

}
