package cz.gattserver.grass.monitor.web;

import cz.gattserver.grass.core.ui.pages.factories.template.AbstractPageFactory;
import org.springframework.stereotype.Component;

@Component("monitorPageFactory")
public class MonitorPageFactory extends AbstractPageFactory {

	public MonitorPageFactory() {
		super("system-monitor");
	}
}
