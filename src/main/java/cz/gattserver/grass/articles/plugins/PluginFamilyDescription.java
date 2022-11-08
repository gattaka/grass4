package cz.gattserver.grass.articles.plugins;

/**
 * Rozhraní popisu rodiny prvku editoru článků
 * 
 * @author gatt
 * 
 */
public interface PluginFamilyDescription {

	/**
	 * Hlavní identifikační metoda
	 * 
	 * @return identifikátor rodiny
	 */
	String getFamily();

	/**
	 * Získá popis k rodině prvků
	 * 
	 * @return HTML popis
	 */
	String getDescription();

}
