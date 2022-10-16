package cz.gattserver.grass.ui.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.services.CoreACLService;
import cz.gattserver.grass.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.ui.pages.settings.AbstractPageFragmentFactory;

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
