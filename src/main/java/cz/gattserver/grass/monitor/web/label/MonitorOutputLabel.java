package cz.gattserver.grass.monitor.web.label;

import com.vaadin.flow.component.html.Div;

public class MonitorOutputLabel extends Div {

	private static final long serialVersionUID = 7228246273667002433L;
	public static final String LOG_STYLE_CLASS = "system-monitor-log-style";

	public MonitorOutputLabel(String value) {
		addClassName(LOG_STYLE_CLASS);
		add(value);
	}
}
