package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.DefaultErrorHandler;
import cz.gattserver.common.exception.SystemException;
import cz.gattserver.grass.core.exception.GrassPageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;

@Tag(Tag.DIV)
public class RouteNotFoundErrorPage extends ErrorPage implements HasErrorParameter<NotFoundException> {

	private static final long serialVersionUID = 4576353466500365046L;

	private static final Logger logger = LoggerFactory.getLogger(RouteNotFoundErrorPage.class);

	@Override
	protected GrassPageException getException() {
		return new GrassPageException(HttpServletResponse.SC_NOT_FOUND);
	}

	@Override
	public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
		init();
		return HttpServletResponse.SC_NOT_FOUND;
	}
}
