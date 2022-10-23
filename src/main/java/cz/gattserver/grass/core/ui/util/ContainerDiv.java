package cz.gattserver.grass.core.ui.util;

import com.vaadin.flow.component.html.Div;

public class ContainerDiv extends Div {

	private static final long serialVersionUID = -948088487069799413L;

	public ContainerDiv() {
		getStyle().set("padding", "var(--lumo-space-m)").set("border", "1px solid lightgray")
				.set("border-radius", "var(--lumo-border-radius-m)").set("overflow-y", "auto")
				.set("white-space", "pre-wrap").set("font-family", "monospace").set("font-size", "10pt")
				.set("tab-size", "4");
	}

}
