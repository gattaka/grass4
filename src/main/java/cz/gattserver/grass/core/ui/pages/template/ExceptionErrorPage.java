package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.*;
import cz.gattserver.grass.core.exception.GrassPageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(Tag.DIV)
public class ExceptionErrorPage extends ErrorPage implements HasErrorParameter<Exception> {

	private static final long serialVersionUID = 4576353466500365046L;

	private static final Logger logger = LoggerFactory.getLogger(ExceptionErrorPage.class);

	private GrassPageException exception;

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
		exception = new GrassPageException(500, parameter.getException());
		init();
		return exception.getStatus();
	}

	@Override
	protected GrassPageException getException() {
		return exception;
	}

}
