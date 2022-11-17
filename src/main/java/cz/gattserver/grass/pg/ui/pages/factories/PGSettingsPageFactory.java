package cz.gattserver.grass.pg.ui.pages.factories;

import cz.gattserver.grass.pg.ui.pages.PGSettingsPageFragmentFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.stereotype.Component;

@Component
public class PGSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public PGSettingsPageFactory() {
		super("Fotogalerie", "photogallery");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new PGSettingsPageFragmentFactory();
	}
}
