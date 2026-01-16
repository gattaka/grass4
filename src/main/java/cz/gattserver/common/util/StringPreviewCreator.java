package cz.gattserver.common.util;

import org.apache.commons.lang3.Validate;

public class StringPreviewCreator {

	private StringPreviewCreator() {
	}

	/**
	 * Upraví text tak, aby pokud přesahuje maximální délku, byl zkrácen a byla
	 * mu místo toho přidána koncovka "..." indikující, že text byl oříznut
	 * 
	 * @param text
	 *            zpracovávaný text
	 * @param maxLength
	 *            maximální délka textu, který ještě nebude zkrácena. Minimální
	 *            délka jsou 3 znaky, aby mělo smysl dělat oříznutí s "..."
	 * @return původní nebo oříznutý text v závislosti na požadované maximální
	 *         délce
	 */
	public static String createPreview(String text, int maxLength) {
		Validate.isTrue(maxLength >= 3);
		if (text.length() > maxLength)
			return text.substring(0, maxLength - 3) + "...";
		else
			return text;
	}
}
