package cz.gattserver.grass.core.ui.pages.template;

import com.vaadin.flow.router.*;
import cz.gattserver.common.ui.ComponentFactory;
import cz.gattserver.grass.core.exception.GrassPageException;
import cz.gattserver.common.exception.SystemException;
import cz.gattserver.grass.core.ui.pages.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.textfield.TextArea;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

@ParentLayout(MainView.class)
public class ErrorPage extends Div implements HasErrorParameter<Exception> {

    private static final Logger logger = LoggerFactory.getLogger(ErrorPage.class);

    private GrassPageException exception;

    @Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<Exception> parameter) {
        Throwable ex = parameter.getException();
        while (true) {
            if (ex.getCause() == null) break;
            ex = ex.getCause();
        }
        if (ex instanceof GrassPageException) {
            exception = (GrassPageException) ex;
        } else {
            exception = new GrassPageException(500, parameter.getException());
        }

        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);
        horizontalLayout.setPadding(true);
        horizontalLayout.setWidthFull();

        Div div = new Div();
        div.setText(getErrorText(exception.getStatus()));
        div.addClassName("error-label");
        Image img = new Image(getErrorImage(exception.getStatus()), "Chyba");

        horizontalLayout.add(img);
        horizontalLayout.add(div);
        horizontalLayout.setFlexGrow(1, div);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);

        layout.add(horizontalLayout);

        if (exception.getStatus() == 500) {
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

        return exception.getStatus();
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
                return "img/403.png";
            case 404:
                return "img/404.png";
            case 500:
            default:
                return "img/500.png";
        }
    }
}