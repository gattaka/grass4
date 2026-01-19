package cz.gattserver.common.vaadin;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;

public class InlineButton extends Div {

    public InlineButton(String text, ComponentEventListener<ClickEvent<Div>> listener) {
        add(text);
        addClickListener(listener);
        addClassName("inline-button");
    }
}