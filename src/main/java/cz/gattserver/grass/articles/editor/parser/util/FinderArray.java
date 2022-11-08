package cz.gattserver.grass.articles.editor.parser.util;

/**
 * Jednoduchá implementace cyklického pole pro hledání řetězce elementu nadpisu
 * 
 * @author Gattaka
 * 
 */
public class FinderArray {

	/**
	 * Jak dlouhé je potřeba cyklické pole pro hledání oddělovače částí ?
	 * Aktuálně postačí 4, protože nadpis je označován jako '[Nx]', kde x je z
	 * {0,9}
	 */
	private static final int DEFAULT_LENGTH = 4;
	private char[] searchBuffer;

	/**
	 * Ukazuje na začátek obsahu. Z počátku, než se pole poprvé naplní, zůstává
	 * na hodnotě 0. Po naplnění začíná krokovat a cyklit společně s vkládacím
	 * indexem
	 */
	private int startPointer = 0;
	/**
	 * Vkládácí index, ukazuje, kam se bude vkládat nový prvek a cyklí, pokud
	 * narazí na konec pole
	 */
	private int insertPointer = 0;
	/**
	 * Indikátor, zda došlo k zaplnění celého pole a má tedy čtecí index začít
	 * krokovat a cyklit
	 */
	private boolean filled = false;

	public FinderArray(int size) {
		searchBuffer = new char[size];
	}

	public FinderArray() {
		this(DEFAULT_LENGTH);
	}

	public void addChar(char c) {
		/**
		 * Vždy se zapisuje na "poslední" pozici, která je současně starou první
		 * (aby startPointer ukazoval na počátek searchBuffer.length-dlouhé
		 * sekvence)
		 */
		searchBuffer[insertPointer] = c;
		insertPointer++;
		if (filled) {
			startPointer = (startPointer + 1) % searchBuffer.length;
		}
		if (insertPointer == searchBuffer.length) {
			filled = true;
			insertPointer = 0;
		}
	}

	public char getChar(int index) {
		return searchBuffer[(startPointer + index) % searchBuffer.length];
	}

	public int getSize() {
		return searchBuffer.length;
	}

}