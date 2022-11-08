package cz.gattserver.grass.articles.editor.parser.impl;

import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.ParsingProcessor;
import cz.gattserver.grass.articles.editor.parser.elements.ArticleElement;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * STRATEGIE NOVÝCH ŘÁDKŮ: - pakliže řádek končí normálně \n a přitom obsahuje
 * nějaký text, vytvoří se pouze odrážka <br/>
 * - tato akce se dá zakázat pomocí flagu (takový table určitě nechce aby tam
 * byly <br/>
 * symboly ve zdrojáku - tato kontrola se zjišťuje z bloku newline, protože se
 * tak eliminuje chyba na straně pluginu, kdyby zapomněl flag znovu otevřít -
 * pakliže je po nějakém EOL_* tokenu symbol EL (empty line), je to vyhodnoceno
 * jako oddělení odstavců - drží se flag o tom, zda se píše odstavec a pokud
 * ano, je tato kombinace tokenů vyhodnocena jako
 * </p>
 * - jinak se tam položí za EOL_* jedno <br/>
 * a za každý EL další <br/>
 * - pakliže se nepíše odstavec, je při prvním možném elementu (zda jsou
 * odstavce povolené se zjišťuje stejně jako zda jsou povolené <br/>
 * symboly) vysázen prvek
 * <p>
 * 
 * @author gatt
 */
public class ArticleParser implements Parser {

	private static Logger logger = LoggerFactory.getLogger(ArticleParser.class);

	private ParsingProcessor parsingProcessor;

	/**
	 * Postaví strom článku a vyhodnotí chyby. Vstupní metoda pro zpracování
	 * článku.
	 */
	public Element parse(ParsingProcessor parsingProcessor) {
		this.parsingProcessor = parsingProcessor;
		parsingProcessor.nextToken();
		return article();
	}

	/**
	 * Kořenový element - article. Vyhodnocuje nic jako prázdný obsah, začátek
	 * tagu nebo text jako blok nebo koncový tag jako chybu
	 * 
	 * @return strom článku
	 */
	private ArticleElement article() {
		logger.debug("article: {}", parsingProcessor.getToken());
		if (Token.EOF.equals(parsingProcessor.getToken())) {
			// Konec článku = prázdný článek
			return new ArticleElement(null);
		} else {
			// Konec řádky (ne článku), počáteční tag, text - zpracuj obsah jako
			// blok elementů a textu - vyrob si list do kterého se budou
			// nalezené a zpracované AST elementů přidávat.
			//
			// Koncový tag bude možná chyba, protože jsem ještě nenašel žádný
			// počáteční, ale protože Lexer nebere v úvahu sémantiku vstupu, tak
			// to může být klidně text protože [/ssss] není třeba tag žádného
			// pluginu, tudíž to není chyba, že tím článek začíná
			List<Element> elist = new ArrayList<>();
			parsingProcessor.getBlock(elist, null);
			return new ArticleElement(elist);
		}
	}
}
