package cz.gattserver.grass.articles.editor.parser.util;

/**
 * Ořezává výsledný text z parseru do tvaru, kde je jenom plain-text obsah.
 * Veškeré formátování a skripty jsou odstraněny. Hledání v takovém textu je pak
 * efektivnější a přesnější (nejsou nacházeny jména tagů apod.)
 * 
 * @author gatt
 * 
 */
public class HTMLTagsFilter {

	private HTMLTagsFilter() {
	}

	/**
	 * <p>
	 * Zbavuje text HTML tagů a vrací čistý text připravený pro search
	 * indexování. Protože některé tagy mohou způsobit oddělení textu, vkládá za
	 * každý tag preventivně mezeru. Protože text není určen ke čtení, ale k
	 * indexování, nevadí to.
	 * </p>
	 * 
	 * <p>
	 * Příklad:
	 * </p>
	 * 
	 * <p>
	 * <strong>Vstup:</strong> proto &lt;strong&gt;požaduji&lt;/strong&gt;
	 * </p>
	 * <p>
	 * <strong>Výstup:</strong> proto &nbsp;požaduji
	 * </p>
	 * 
	 * @param text
	 *            vstupní text, ve kterém můžou být HTML tagy
	 * @return vyčištěný text, ve kterém je jenom obsah bez formátování
	 */
	public static String trim(String text) {
		boolean trimMode = false;
		boolean endTagOccured = false;
		StringBuilder stringBuilder = new StringBuilder();
		for (int index = 0; index < text.length(); index++) {
			char c = text.charAt(index);
			if (c == '<') {
				// započal tag - zapni trim
				trimMode = true;
			} else if (c == '>') {
				// skončil tag - vypni trim
				trimMode = false;
				endTagOccured = true;
			} else if (!trimMode) {
				// pokud jedu trim, ignoruj znaky, jinak je připisuj
				if (endTagOccured) {
					endTagOccured = false;
					stringBuilder.append(' ');
				}
				stringBuilder.append(c);
			}
		}
		return stringBuilder.toString();
	}

}
