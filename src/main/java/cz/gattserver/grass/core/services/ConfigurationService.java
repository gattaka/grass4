package cz.gattserver.grass.core.services;

import cz.gattserver.grass.core.config.AbstractConfiguration;

public interface ConfigurationService {

	/**
	 * Nahraje existující konfiguraci a dopíše její vyplněné hodnoty do
	 * předaného objektu konfigurace
	 * 
	 * @param configuration
	 */
	public void loadConfiguration(AbstractConfiguration configuration);

	/**
	 * Uloží konfiguraci.
	 * 
	 * @param configuration
	 *            objekt konfigurace
	 */
	public void saveConfiguration(AbstractConfiguration configuration);
}
