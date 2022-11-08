package cz.gattserver.grass.articles.editor.lexer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cz.gattserver.grass.articles.editor.lexer.Token.*;

/**
 * 
 * @author gatt
 */
public class Lexer {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private String source;

	// pozice na řádce
	private int index;

	// zkoumaný znak
	private int ch;

	// pokud se právě skočilo přes breakline, musí
	// se s inkrementací col počkat, jinak by byl
	// napřed
	private boolean br = false;

	// načtený identifikátor
	private StringBuilder word = new StringBuilder();

	/**
	 * Bere zdrojový text a seká ho na tokeny
	 * 
	 * @param source
	 *            zdrojový text
	 * @param debugOutput
	 */
	public Lexer(String source) {
		this.source = source == null ? "" : source;
		ch = nextChar(); // naber první znak
	}

	/**
	 * Získá začáteční tag bez závorek '[' a ']' - ověřuje nicméně jestli je to
	 * opravdu tag a jestli je počáteční
	 * 
	 * @return text tagu
	 */
	public String getStartTag() {
		// chci jenom text tagu
		if (word.length() > 2 && word.charAt(0) == '[' && word.charAt(word.length() - 1) == ']'
				&& word.charAt(1) != '/') {
			return word.substring(1, word.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * Získá koncový tag bez závorek '[' a ']' - ověřuje nicméně jestli je to
	 * opravdu tag a jestli je koncový
	 * 
	 * @return text tagu
	 */
	public String getEndTag() {
		// chci jenom text tagu
		if (word.length() > 3 && word.charAt(0) == '[' && word.charAt(word.length() - 1) == ']'
				&& word.charAt(1) == '/') {
			return word.substring(2, word.length() - 1);
		} else {
			return "";
		}
	}

	/**
	 * Získá text. Myšleno jako celý načtený text, který nebyl rozpoznán jako
	 * tag
	 * 
	 * @return text tagu
	 */
	public String getText() {
		return word.toString();
	}

	/**
	 * Přečte a vrátí jeden znak nebo záporné číslo které udává jaká chyba
	 * nastala
	 * 
	 * @return znak pokud vše dopadlo dobře nebo -1 pokud došly znaky a -2 pokud
	 *         je konec řádky
	 */
	private int nextChar() {
		if (index == source.length())
			return -1;
		// index se inkrementuje vždy
		ch = source.charAt(index);
		index++;
		if (ch == '\n' || ch == '\r') {
			if (ch == '\r')
				index++;
			br = true;
			return -2;
		}
		// Údaj o sloupci se inkrementuje jenom pokud před ním nebyl konec řádky
		if (br)
			br = false;
		return ch;
	}

	/**
	 * Provede jednu iteraci a vrátí token
	 * 
	 * @return {@link cz.gattserver.grass.articles.editor.lexer.Token} token, který našel
	 */
	public cz.gattserver.grass.articles.editor.lexer.Token nextToken() {
		cz.gattserver.grass.articles.editor.lexer.Token symbol = readNextToken();
		logger.debug("LEXER: Token made -> {}", symbol);
		return symbol;
	}

	private cz.gattserver.grass.articles.editor.lexer.Token readNextToken() {
		word.setLength(0);

		// konec řádku - \n
		if (ch == -2) {
			ch = nextChar(); // musím se ale posunout !!!
			return EOL;
		}

		// tabulátor - \t
		if (ch == '\t') {
			ch = nextChar(); // musím se ale posunout !!!
			return TAB;
		}

		// pokud jsem na konci souboru, tak to sdělím okamžitě
		if (ch == -1)
			return EOF;

		// Tag - musí začínat '[', končit ']' a obsahovat pouze písmena, čísla
		// nebo '_'
		if (ch == '[') {
			cz.gattserver.grass.articles.editor.lexer.Token token = readTagToken();
			if (token != null)
				return token;
		}

		// Jinak je to text -- dokud nedojde ke změně, pokračuj ve čtení znaků
		while ((ch != '[') && (ch != -1) && (ch != -2) && (ch != '\t')) {
			word.append((char) ch);
			ch = nextChar();
		}

		// dočetl jsem text
		return TEXT;
	}

	private Token readTagToken() {
		// zkontroluj vnitřek tagu
		do {
			word.append((char) ch);
			ch = nextChar();

			// beru podtržítka,
			// čísla a písmena,
			// lomítko, ale pouze pokud je na pozici 1 ~> [/TAG]
			// a tag nesmí být roztaženej mezi řádky
		} while (Character.isLetterOrDigit(ch) || ch == '_' || (ch == '/' && word.length() == 1));

		// čtení bylo ukončeno kvůli konci řádku nebo EOF - ukonči řádku
		// (byl to text, nekončilo se ']')
		if (ch == -1 || ch == -2)
			return TEXT;

		// zkontroluj ']'
		if (ch == ']') {
			word.append((char) ch);
			ch = nextChar();

			// pokud je tag vlastně prázdný, tak to není tag, ale text
			if ((!word.toString().equals("[]")) && (!word.toString().equals("[/]"))) {

				// pokud tag začínal [/ tak to byl koncový tag
				return word.charAt(1) == '/' ? END_TAG : START_TAG;
			}
		}

		// pokud mne vyhodila značna tagu, tak ukončím tohle jako text
		// ale nebudu jí načítat - mohl bych přejet tag
		if (ch == '[')
			return TEXT;

		// jinak to nechám dojet jako text
		return null;
	}

}
