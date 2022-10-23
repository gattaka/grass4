package cz.gattserver.grass.core.ui.pages.settings.factories;

import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("applicationSettingsPageFactory")
public class ApplicationSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACLService coreACL;

	public ApplicationSettingsPageFactory() {
		super("Aplikace", "app");
	}

	public boolean isAuthorized() {
		return coreACL.canShowApplicationSettings(getUser());
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new ApplicationSettingsPageFragmentFactory();
	}
}
