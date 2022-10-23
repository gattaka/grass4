package cz.gattserver.grass.core.ui.pages.settings.factories;

import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("usersSettingsPageFactory")
public class UsersSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACLService coreACL;

	public UsersSettingsPageFactory() {
		super("Uživatelé", "users");
	}

	public boolean isAuthorized() {
		return coreACL.canShowUserSettings(getUser());
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new UsersSettingsPageFragmentFactory();
	}

}
