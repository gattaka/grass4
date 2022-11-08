package cz.gattserver.grass.articles.editor.parser;

import cz.gattserver.grass.articles.editor.lexer.Lexer;
import cz.gattserver.grass.articles.editor.lexer.Token;
import cz.gattserver.grass.articles.editor.parser.elements.*;
import cz.gattserver.grass.articles.editor.parser.exceptions.ParserException;
import cz.gattserver.grass.articles.editor.parser.exceptions.TokenException;
import cz.gattserver.grass.articles.plugins.Plugin;
import cz.gattserver.grass.articles.util.HTMLEscaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static cz.gattserver.grass.articles.editor.lexer.Token.*;

/**
 * <p>
 * Každý {@link cz.gattserver.grass.articles.editor.parser.Parser} v sobě může mít obsahy jiných {@link Plugin}ů, proto je
 * potřeba aby měl možnost si zavolat o překlad nějaké části svého obsahu jiný
 * plugin.
 * </p>
 * <p>
 * Protože ale rozhodování, který plugin na co zavolat náleží centrálnímu
 * parseru, je to řešené takto pomocí předávání tohoto objektu, který volá
 * pluginy na základně žádností provádějících pluginů, plus si drží informace
 * jako pozice v textu apod.
 * </p>
 * 
 * @author gatt
 */
public class ParsingProcessor {

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	private Map<String, Plugin> registerSnapshot;
	private Token token;
	private Lexer lexer;

	/**
	 * některé pluginy potřebují sázet linky a u těch je občas potřeba znát
	 * kořenovou adresu
	 */
	private String contextRoot;

	/**
	 * zásobník aktivovaných pluginů - dalo by se to řešit automaticky pomocí
	 * předávání instancí {@link ParsingProcessor}, ale to má stejný význam,
	 * navíc to ještě na zásobník (systémový) ukládá kvantum věcí navíc, tohle
	 * je úspornější
	 */
	private Deque<StackElement> activePlugins;

	/**
	 * položka stacku
	 */
	private class StackElement {

		/**
		 * Jaký tag se právě řeší
		 */
		private String tag;

		/**
		 * Jeho parser
		 */
		private cz.gattserver.grass.articles.editor.parser.Parser parserPlugin;

		public cz.gattserver.grass.articles.editor.parser.Parser getParserPlugin() {
			return parserPlugin;
		}

		public String getTag() {
			return tag;
		}

		private StackElement(cz.gattserver.grass.articles.editor.parser.Parser parserPlugin, String tag) {
			this.tag = tag;
			this.parserPlugin = parserPlugin;
		}

		@Override
		public String toString() {
			return tag;
		}

	}

	public ParsingProcessor(Lexer lexer, String contextRoot, Map<String, Plugin> registerSnapshot) {
		this.lexer = lexer;
		this.contextRoot = contextRoot;
		this.activePlugins = new LinkedList<>();
		this.registerSnapshot = registerSnapshot;
	}

	/**
	 * Pro pluginy, které pracují s linky a obrázky je potřeba občas uvádět
	 * kořen webu, ke kterému potom budou
	 * 
	 * @return kořen webu
	 */
	public String getContextRoot() {
		return contextRoot;
	}

	/**
	 * Zjistí jaký token se právě zpracovává
	 * 
	 * @return {@link Token } dle toho, co právě našel
	 * @see {@link Token }
	 */
	public Token getToken() {
		return token;
	}

	/**
	 * Zpracuje další {@link Token }
	 */
	public void nextToken() {
		this.token = lexer.nextToken();
	}

	/**
	 * Vrátí naparsovaný obsah jako plain-text
	 * 
	 * @return text
	 */
	public String getCode() {
		return lexer.getText();
	}

	/**
	 * Vrátí naparsovaný obsah jako escapovaný plain-text
	 * 
	 * @return text
	 */
	public String getText() {
		return HTMLEscaper.stringToHTMLString(getCode());
	}

	/**
	 * Vrátí naparsovaný obsah jako by to byl počáteční tag - odstřihne od něj
	 * tedy počáteční a koncovou hranatou závorku
	 * 
	 * @return počáteční tag
	 * @throws TokenException
	 *             pokud není akutální token {@link Token#START_TAG}
	 */
	public String getStartTag() {
		if (!Token.START_TAG.equals(token))
			throw new TokenException(Token.START_TAG, token, lexer.getText());
		return lexer.getStartTag();
	}

	/**
	 * Vrátí naparsovaný obsah jako by to byl koncový tag - odstřihne od něj
	 * tedy počáteční a koncovou hranatou závorku a lomítko
	 * 
	 * @return koncový tag
	 * @throws TokenException
	 *             pokud není akutální token {@link Token#END_TAG}
	 */
	public String getEndTag() {
		if (!Token.END_TAG.equals(token))
			throw new TokenException(Token.END_TAG, token, lexer.getText());
		return lexer.getEndTag();
	}

	/**
	 * ParserCore
	 */
	private Element parseTag() {
		String tag = getStartTag();
		logger.debug("Looking for the right ParserPlugin for tag '{}'", tag);

		String activePluginsMsg = "activePlugins: {}";

		Plugin plugin = registerSnapshot.get(tag);
		if (plugin != null) {
			Parser parser = plugin.getParser();
			try {
				// vstupuješ do dalšího patra parsovacího stromu
				// => nastav si, že tento plugin je právě u prohledávání
				activePlugins.push(new StackElement(parser, tag));
				logger.debug(parser.getClass() + " was pushed in stack and launched");
				logger.debug(activePluginsMsg, activePlugins);

				// Spusť plugin
				Element elementTree = parser.parse(this);

				parser = activePlugins.pop().getParserPlugin();
				logger.debug(parser.getClass() + " terminates (clean) and was poped from stack");
				logger.debug(activePluginsMsg, activePlugins);

				return elementTree;
			} catch (TokenException ex) {
				// Plugin běží, ale je problém s očekávanou posloupností Tokenů
				parser = activePlugins.pop().getParserPlugin();
				logger.debug(parser.getClass() + " terminates (token exception) and was poped from stack");
				logger.debug("activePlugins: " + activePlugins);
				return new ParserErrorElement(tag, ex.toString(), activePlugins.toString());
			} catch (ParserException pe) {
				// Plugin běží, ale došlo v něm k nějaké jiné chybě
				parser = activePlugins.pop().getParserPlugin();
				logger.warn(parser.getClass() + " terminates (parse exception) and was poped from stack");
				logger.warn(activePluginsMsg, activePlugins);
				return new ParserErrorElement(tag, pe.getMessage(), activePlugins.toString());
			} catch (Exception ex) {
				// Došlo k chybě
				parser = activePlugins.pop().getParserPlugin();
				logger.error(parser.getClass() + " terminates (plugin exception) and was poped from stack");
				logger.error(activePluginsMsg, activePlugins);
				logger.error("Plugin error", ex);
				return new PluginErrorElement(tag);
			}
		} else {
			logger.debug("ParserPlugin for tag '{}' not found, '{}' is a text", tag, tag);
		}

		return null; // jinak vrať null - žádný plugin tohle nezná
	}

	/**
	 * Zpracuje obsah jako podstrom prvků. Parsuje, dokud nenarazí na zadaný
	 * {@link Token#END_TAG} ukončovací tag nebo na {@link Token#EOF}. Jiné
	 * ukončovací tagy bere jako text nebo jako chybu předčasného ukončení.
	 * 
	 * @param elist
	 *            list do kterého se budou ukládat výsledné podstromy prvků
	 * @param stopEndTag
	 *            ukončovací tag, na kterém se má zastavit parsování. Může být
	 *            <code>null</code>, v tom případě zpracovávám až do
	 *            {@link Token#EOF}
	 * @throws TokenException
	 *             pokud najde předčasně ukončovací tag některého z předchozích
	 *             aktivních pluginů (v rámci zpracování jejich podstromu)
	 */
	public void getBlock(List<Element> elist, String stopEndTag) {
		logger.debug("block: " + getToken());
		switch (getToken()) {
		case START_TAG:
		case TEXT:
		case TAB:
		case EOL:
			elist.add(getElement());
			getBlock(elist, stopEndTag);
			break;
		case END_TAG:
			String actualEndTag = getEndTag();
			if (stopEndTag != null) {
				if (actualEndTag.equals(stopEndTag)) {
					// ukončil jsem v pořádku parsování obsahu jako podstrom
					// prvků,
					// ukonči blok
					break;
				} else {
					boolean isInActive = activePlugins.stream().map(StackElement::getTag)
							.anyMatch(tag -> tag.equals(actualEndTag));
					// nejde náhodou o ukončovací tag některého z aktivních
					// pluginů?
					// Pokud ano, pak to ber jako chybu. Pokud ne, pak
					// ho ber jako text a parsuj obsah dál, jako prvky jeho
					// podstromu.
					// Tím je umožněno, aby se dalo napsat například
					// [N1][/TEST][/N1], ale zároveň aby se dali lokalizovat
					// chyby
					if (isInActive)
						throw new TokenException(stopEndTag, actualEndTag);
				}
			}
			elist.add(getTextTree());
			getBlock(elist, stopEndTag);
			break;
		case EOF:
		default:
			if (!activePlugins.isEmpty())
				throw new TokenException(Token.END_TAG, activePlugins.peek().getTag());
			break;
		}

	}

	private BreaklineElement getBreakline() {
		logger.debug("breakline: " + getToken());
		nextToken();
		return new BreaklineElement();
	}

	/**
	 * Zpracuje element
	 * 
	 * @return
	 */
	public Element getElement() {
		logger.debug("element: " + getToken());
		switch (getToken()) {
		case START_TAG:
		case END_TAG:
			Element element = parseTag();
			return element == null ? getTextTree() : element;
		case TEXT:
		case TAB:
			return getTextTree();
		case EOL:
			return getBreakline();
		case EOF:
		default:
			logger.warn("Čekal jsem jeden z [{}], ne {}", new Token[] { START_TAG, END_TAG, TEXT, EOL }, getToken());
			throw new ParserException();
		}
	}

	/**
	 * Zpracuje obsah jako obyčejný text - nebezpečné znaky zaescapuje
	 * 
	 * @return escapovaný text TextTree AST
	 */
	public TextElement getTextTree() {
		return getTextTree(true);
	}

	private TextElement getTextTree(boolean escaped) {
		logger.debug("text: " + getToken());
		String text = escaped ? getText() : getCode();
		nextToken();
		return new TextElement(text);
	}

	/**
	 * Zpracuje obsah jako by to byl kód - nebude escapovat obsah
	 * 
	 * @return neescapovaný {@link TextElement} AST
	 */
	public TextElement getCodeTextTree() {
		return getTextTree(false);
	}
}
