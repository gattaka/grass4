package cz.gattserver.grass.print3d.ui.pages.factories;

import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import cz.gattserver.grass.print3d.ui.pages.Print3dSettingsPageFragmentFactory;
import org.springframework.stereotype.Component;

@Component
public class Print3dSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	public Print3dSettingsPageFactory() {
		super("Print3d", "print3d");
	}

	public boolean isAuthorized() {
		return getUser().isAdmin();
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new Print3dSettingsPageFragmentFactory();
	}
}
