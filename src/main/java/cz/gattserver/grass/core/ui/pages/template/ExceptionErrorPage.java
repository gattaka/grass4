package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import cz.gattserver.common.exception.SystemException;
import cz.gattserver.grass.core.exception.GrassPageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Tag(Tag.DIV)
public class ExceptionErrorPage extends ErrorPage implements HasErrorParameter<GrassPageException> {

	private static final long serialVersionUID = 4576353466500365046L;

	private static final Logger logger = LoggerFactory.getLogger(ExceptionErrorPage.class);

	private GrassPageException exception;

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<GrassPageException> parameter) {
		exception = parameter.getException();
		init();
		return exception.getStatus();
	}

	@Override
	protected void createColumnContent(Div layout) {
		super.createColumnContent(layout);
		TextArea detailsArea = new TextArea();
		String log = new SystemException("V aplikaci došlo k neočekávané chybě", exception).toString();
		logger.error(log);
		detailsArea.setValue(log);
		detailsArea.setEnabled(true);
		detailsArea.setReadOnly(true);
		detailsArea.setWidthFull();
		detailsArea.addClassName("error-text-field");
		detailsArea.setHeight("500px");
		detailsArea.getStyle().set("margin-top", "10px");
		layout.add(detailsArea);
	}

	@Override
	protected String getErrorText() {
		return "500 - Došlo k chybě na straně serveru";
	}

	@Override
	protected String getErrorImage() {
		return "VAADIN/img/500.png";
	}
}