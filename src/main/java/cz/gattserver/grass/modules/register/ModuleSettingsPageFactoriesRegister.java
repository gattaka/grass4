package cz.gattserver.grass.modules.register;

import cz.gattserver.grass.ui.pages.settings.ModuleSettingsPageFactory;

public interface ModuleSettingsPageFactoriesRegister {

	public ModuleSettingsPageFactory getFactory(String name);

	/**
	 * Tvorba aliasů
	 * 
	 * @param settingsName
	 *            název nastavení
	 * @param factory
	 *            factory pro tvorbu stránky nastavení
	 * @return zaregistrovaná factory
	 */
	public ModuleSettingsPageFactory putAlias(String settingsName, ModuleSettingsPageFactory factory);

}
