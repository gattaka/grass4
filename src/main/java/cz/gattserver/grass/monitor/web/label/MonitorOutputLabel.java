package cz.gattserver.grass.monitor.web.label;

import com.vaadin.flow.component.html.Div;

public class MonitorOutputLabel extends Div {

	public static final String LOG_STYLE_CLASS = "system-monitor-log-style";

	public MonitorOutputLabel(String value) {
		addClassName(LOG_STYLE_CLASS);
		add(value);
	}
}
