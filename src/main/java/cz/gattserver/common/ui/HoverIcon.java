package cz.gattserver.common.ui;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

/**
 * <a href="https://vaadin.com/tutorials/vaadin-key-concepts#_interacting_with_javascript_events_and_dom_nodes">https://vaadin.com/tutorials/vaadin-key-concepts#_interacting_with_javascript_events_and_dom_nodes</a>
 */
public class HoverIcon extends Icon {
    @Serial
    private static final long serialVersionUID = -2137206582218756436L;

    public HoverIcon(VaadinIcon icon) {
        super(icon);
    }

    public void changeIcon(VaadinIcon icon) {
        getElement().setAttribute("icon", "vaadin:" + icon.name().toLowerCase().replace('_', '-'));
    }
}