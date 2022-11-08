package cz.gattserver.grass.articles.editor.parser;

import java.util.Set;

/**
 * Kontext pouzity pri generovani kodu.
 */
public interface Context {

	/**
	 * Zapíše do výstupu
	 * 
	 * @param msg
	 */
	void print(String msg);

	/**
	 * Zapíše do výstupu a ukončí novou řádkou
	 * 
	 * @param msg
	 */
	void println(String msg);

	/**
	 * Nastaví úroveň textu dle nadpisu (1-4)
	 * 
	 * @param level
	 */
	void setHeaderLevel(int level);

	/**
	 * Získá identifikátor nadpisu dle pořadí (na upravování obsahu po částech)
	 */
	int getNextHeaderIdentifier();

	/**
	 * Vyresetuje úroveň nadpisu
	 */
	void resetHeaderLevel();

	/**
	 * Vytvoří finální výstup
	 * 
	 * @return výstup článku
	 */
	String getOutput();

	/**
	 * Zaregistruje CSS zdroj, který je potřeba aby systém přidal při
	 * zobrazování článku s tímto pluginem
	 * 
	 * @param url
	 *            místo na které se má odkázat
	 */
	void addCSSResource(String url);

	/**
	 * Zaregistruje JS zdroj, který je potřeba aby systém přidal při zobrazování
	 * článku s tímto pluginem
	 * 
	 * @param url
	 *            místo na které se má odkázat
	 */
	void addJSResource(String url);

	/**
	 * Získá CSS zdroje, potřebné pro korektní zobrazení tohoto článku
	 * 
	 * @return místa zdrojů
	 */
	Set<String> getCSSResources();

	/**
	 * Získá JS zdroje, potřebné pro korektní zobrazení tohoto článku
	 * 
	 * @return místa zdrojů
	 */
	Set<String> getJSResources();

	void addJSCode(String code);

	Set<String> getJSCodes();

}
