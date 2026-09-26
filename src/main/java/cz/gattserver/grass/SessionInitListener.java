package cz.gattserver.grass;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinSession;
import cz.gattserver.common.exception.ApplicationErrorHandler;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SessionInitListener {

	@EventListener
	public void errorHandlerRegister(ServiceInitEvent event) {
		event.getSource().addSessionInitListener(
				sessionInitEvent -> VaadinSession.getCurrent().setErrorHandler(new ApplicationErrorHandler()));
	}

}