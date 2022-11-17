package cz.gattserver.grass.fm;

/**
 * Stav zpracování souboru
 */
public enum FileProcessState {
	
	/**
	 * V pořádku, nalezen
	 */
	SUCCESS,

	/**
	 * Nebyl nalezen
	 */
	MISSING,

	/**
	 * Je vyžadován adresář, ale byl nalezen soubor, který není adresářem
	 */
	DIRECTORY_REQUIRED,

	/**
	 * Existuje, ale podtéká kořenový adresář FM modulu, což je porušení
	 * security omezení
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