package cz.gattserver.grass.core.ui.pages;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import cz.gattserver.common.ui.ComponentFactory;

@PageTitle("Gattserver")
@Route(value = "noservice", layout = MainView.class)
public class NoServicePage extends Div {

    public NoServicePage() {
        removeAll();
        ComponentFactory componentFactory = new ComponentFactory();

        Div layout = componentFactory.createOneColumnLayout();
        add(layout);

        layout.add(new Span("Chybí služba pro čtení tohoto typu obsahu"));
    }
}