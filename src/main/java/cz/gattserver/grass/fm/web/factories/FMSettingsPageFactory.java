package cz.gattserver.grass.fm.web.factories;

import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.fm.web.FMSettingsPageFragmentFactory;
import org.springframework.stereotype.Component;

@Component
public class FMSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public FMSettingsPageFactory() {
		super("Soubory", "fm");
	}

	public boolean isAuthorized() {
		if (getUser() == null)
			return false;
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new FMSettingsPageFragmentFactory();
	}

}
