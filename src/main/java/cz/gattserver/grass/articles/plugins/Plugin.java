package cz.gattserver.grass.articles.plugins;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;

/**
 * Rozhraní prvku editoru článků - pluginu
 * 
 * @author gatt
 * 
 */
public interface Plugin {

	/**
	 * Hlavní identifikační metoda
	 * 
	 * @return identifikátor elemetu - jeho tag, musí být unikátní mezi
	 *         ostatními elementy jinak bude při překladu docházet ke kolizím
	 */
	String getTag();

	/**
	 * Získá instanci parseru
	 * 
	 * @return instance {@link Parser}
	 */
	Parser getParser();

	/**
	 * Získá zdroje pro vytvoření odpovídajícího tlačítka pluginu v editoru
	 * 
	 * @return
	 */
	EditorButtonResourcesTO getEditorButtonResources();

}
