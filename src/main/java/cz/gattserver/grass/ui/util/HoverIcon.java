package cz.gattserver.grass.ui.util;

import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

/**
 * https://vaadin.com/tutorials/vaadin-key-concepts#_interacting_with_javascript_events_and_dom_nodes
 */
public class HoverIcon extends Icon {
	private static final long serialVersionUID = -1891984510176058224L;

	public HoverIcon(VaadinIcon icon) {
		super(icon);
	}

	public void changeIcon(VaadinIcon icon) {
		getElement().setAttribute("icon", "vaadin:" + icon.name().toLowerCase().replace('_', '-'));
	}

}
