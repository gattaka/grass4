package cz.gattserver.grass.articles.plugins.favlink.settings.factories;

import cz.gattserver.grass.articles.plugins.favlink.settings.FavlinkSettingsPageFragmentFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.stereotype.Component;

@Component
public class FavlinkSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public FavlinkSettingsPageFactory() {
		super("Favlink", "favlink");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new FavlinkSettingsPageFragmentFactory();
	}
}
