package cz.gattserver.grass.monitor.tabs.factories;

import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.monitor.tabs.MonitorSettingsPageFragmentFactory;

@Component
public class MonitorSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public MonitorSettingsPageFactory() {
		super("System monitor", "system-monitor");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new MonitorSettingsPageFragmentFactory();
	}
}
