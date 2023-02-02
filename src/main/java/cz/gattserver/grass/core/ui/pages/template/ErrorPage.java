package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.router.*;
import com.vaadin.flow.router.internal.DefaultErrorHandler;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.common.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextArea;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@Tag(Tag.DIV)
public abstract class ErrorPage extends OneColumnPage{

	private static final long serialVersionUID = 4576353466500365046L;

	private static final Logger logger = LoggerFactory.getLogger(ErrorPage.class);

	protected abstract GrassPageException getException();

	@Override
	protected void createColumnContent(Div layout) {
		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSpacing(true);
		horizontalLayout.setPadding(true);
		horizontalLayout.setWidthFull();

		Div div = new Div();
		div.setText(getErrorText(getException().getStatus()));
		div.addClassName("error-label");
		Image img = new Image(getErrorImage(getException().getStatus()), "Chyba");

		horizontalLayout.add(img);
		horizontalLayout.add(div);
		horizontalLayout.setFlexGrow(1, div);
		horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

		layout.add(horizontalLayout);

		if (getException().getStatus() == 500) {
			TextArea detailsArea = new TextArea();
			String log = new SystemException("V aplikaci došlo k neočekávané chybě", getException()).toString();
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
	}

	protected String getErrorText(int status) {
		switch (status) {
			case 403:
				return "403 - Nemáte oprávnění k provedení této operace";
			case 404:
				return "404 - Hledaný obsah neexistuje";
			case 500:
			default:
				return "500 - Došlo k chybě na straně serveru";
		}
	}

	protected String getErrorImage(int status) {
		switch (status) {
			case 403:
				return "VAADIN/img/403.png";
			case 404:
				return "VAADIN/img/404.png";
			case 500:
			default:
				return "VAADIN/img/500.png";
		}
	}
}
