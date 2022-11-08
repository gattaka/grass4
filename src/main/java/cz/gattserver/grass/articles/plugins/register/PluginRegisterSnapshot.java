package cz.gattserver.grass.articles.plugins.register;

import cz.gattserver.grass.articles.plugins.Plugin;

/**
 * Registr pluginů dle tagu, který je vytvářen službou. Funkce by mohla být celá
 * ve službě registru, ale potom by bylo vyžadováno, aby všechny pluginy při
 * svých testech byly spuštěny ve spring contextu, ve kterém by si injektovali
 * prázdnou {@link PluginRegisterService}. Takhle jim
 * {@link PluginRegisterService} akorát vytvoří svůj snapshot jako obyčejnou
 * immutable třídu, se kterou můžou pluginy pracovat bez omezení.
 * 
 * @author Hynek
 *
 */
public interface PluginRegisterSnapshot {

	boolean isRegistered(String tag);

	Plugin get(String tag);

}
