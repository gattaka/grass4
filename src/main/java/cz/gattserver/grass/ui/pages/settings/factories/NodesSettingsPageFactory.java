package cz.gattserver.grass.ui.pages.settings.factories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.services.CoreACLService;
import cz.gattserver.grass.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.ui.pages.settings.AbstractPageFragmentFactory;

@Component("nodesSettingsPageFactory")
public class NodesSettingsPageFactory extends AbstractModuleSettingsPageFactory {

	@Autowired
	private CoreACLService coreACL;

	public NodesSettingsPageFactory() {
		super("Kategorie", "categories");
	}

	public boolean isAuthorized() {
		return coreACL.canShowCategoriesSettings(getUser());
	}

	@Override
	protected AbstractPageFragmentFactory createPageFragmentFactory() {
		return new NodesSettingsPageFragmentFactory();
	}

}
