package cz.gattserver.grass.articles;

/**
 * Stav zpracování souboru
 */
public enum AttachmentsState {

	/**
	 * V pořádku
	 */
	SUCCESS,

	/**
	 * Existuje, ale podtéká kořenový adresář, což je porušení security omezení
	 */
	NOT_VALID,

	/**
	 * Cílový soubor existuje, nelze vytvořit/přesunout/kopírovat
	 */
	ALREADY_EXISTS,

	/**
	 * Operace se nezdařila kvůli systémové chybě
	 */
	SYSTEM_ERROR
}