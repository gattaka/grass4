package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import cz.gattserver.grass.core.exception.GrassPageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(Tag.DIV)
public class GrassPageExceptionErrorPage extends ErrorPage implements HasErrorParameter<GrassPageException> {

	private static final long serialVersionUID = 4576353466500365046L;

	private static final Logger logger = LoggerFactory.getLogger(GrassPageExceptionErrorPage.class);

	private GrassPageException exception;

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<GrassPageException> parameter) {
		exception = parameter.getException();
		init();
		return exception.getStatus();
	}

	@Override
	protected GrassPageException getException() {
		return exception;
	}

}
