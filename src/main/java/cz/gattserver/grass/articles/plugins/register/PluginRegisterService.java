package cz.gattserver.grass.articles.plugins.register;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.Plugin;

import java.util.Map;
import java.util.Set;

/**
 * Registr pluginů, figurující jako sběrné místo, ke kterému se přihlašují
 * všechny pluginy.
 * 
 * @author gatt
 */
public interface PluginRegisterService {

	/**
	 * Získá množinu tagů, které jsou od pluginů zaregistrované
	 * 
	 * @return množina tagů
	 */
	Set<String> getRegisteredTags();

	/**
	 * Získá názvy skupin pluginů, do kterých jsou pluginy zaregistrované
	 * 
	 * @return množina skupin
	 */
	Set<String> getRegisteredFamilies();

	/**
	 * Získá popis (optional) k rodině prvků
	 * 
	 * @param family
	 *            rodina prvků
	 * @return popis nebo <code>null</code>
	 */
	String getFamilyDescription(String family);

	/**
	 * Získá zdroje na vytváření tlačítek v editoru pro danou skupinu tagů
	 * 
	 * @param group
	 *            název skupiny, od jejíž pluginů chci zdroje pro editor
	 * @return množina zdrojů pro editor
	 */
	Set<EditorButtonResourcesTO> getTagResourcesByFamily(String group);

	/**
	 * Mapa pluginů dle tagu, který je vytvářen službou. Funkce by mohla být
	 * celá ve službě registru, ale potom by bylo vyžadováno, aby všechny
	 * pluginy při svých testech byly spuštěny ve spring contextu, ve kterém by
	 * si injektovali prázdnou {@link PluginRegisterService}. Takhle jim
	 * {@link PluginRegisterService} akorát vytvoří svůj snapshot jako obyčejnou
	 * immutable třídu, se kterou můžou pluginy pracovat bez omezení.
	 * 
	 * @return snapshot registru
	 */
	Map<String, Plugin> createRegisterSnapshot();

}
