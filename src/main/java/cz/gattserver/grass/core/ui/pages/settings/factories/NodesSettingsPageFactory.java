package cz.gattserver.grass.core.ui.pages.settings.factories;

import cz.gattserver.grass.core.services.CoreACLService;
import cz.gattserver.grass.core.ui.pages.settings.AbstractModuleSettingsPageFactory;
import cz.gattserver.grass.core.ui.pages.settings.AbstractPageFragmentFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
