package cz.gattserver.grass.hw.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.hw.ui.pages.HWSettingsPageFragmentFactory;

@Component
public class HWSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public HWSettingsPageFactory() {
		super("Evidence HW", "hw");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new HWSettingsPageFragmentFactory();
	}
}
