package cz.gattserver.grass.articles.editor.lexer;

/**
 * @author gatt
 */
public enum Token {

	/**
	 * Počáteční tag
	 */
	START_TAG,

	/**
	 * Koncový tag
	 */
	END_TAG,

	/**
	 * Text
	 */
	TEXT,

	/**
	 * Tabulátor
	 */
	TAB,
	
	/**
	 * Konec řádku
	 */
	EOL,

	/**
	 * Konec souboru
	 */
	EOF
}
