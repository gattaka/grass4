package cz.gattserver.grass.monitor.web.label;

import com.vaadin.flow.component.html.Div;

public class MonitorStateLabel extends Div {

	public MonitorStateLabel(String style, String text) {
		addClassName(style);
		add(text);
	}
}
