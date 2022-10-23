package cz.gattserver.grass.campgames.ui.pages.factories;

import cz.gattserver.grass.campgames.ui.pages.CampgamesSettingsPageFragmentFactory;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;

@Component
public class CampgamesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public CampgamesSettingsPageFactory() {
		super("Evidence táborových her", "campgames");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new CampgamesSettingsPageFragmentFactory();
	}
}
