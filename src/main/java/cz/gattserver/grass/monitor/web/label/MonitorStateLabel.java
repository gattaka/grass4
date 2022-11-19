package cz.gattserver.grass.monitor.web.label;

import com.vaadin.flow.component.html.Div;

public class MonitorStateLabel extends Div {

	private static final long serialVersionUID = 7228246273667002433L;

	public MonitorStateLabel(String style, String text) {
		addClassName(style);
		add(text);
	}
}
