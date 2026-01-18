package cz.gattserver.grass.core.ui.components.button;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import cz.gattserver.grass.core.ui.pages.LoginDialog;

public class InlineButton extends Div {

    public InlineButton(String text, ComponentEventListener<ClickEvent<? extends Component>> listener) {
        add(text);
        addClickListener(e -> new LoginDialog().open());
        addClassName("inline-button");
    }
}
