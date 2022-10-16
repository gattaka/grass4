package cz.gattserver.web.common.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class URLPathAnalyzer implements Serializable {

	private static final long serialVersionUID = -3905355086948814995L;

	private List<String> tokens = new ArrayList<>();
	private StringBuilder buffer = new StringBuilder();

	private int currentIndex;

	/**
	 * aplikuje oddělení částí - lomítko, nebo konec textu
	 */
	private void applyDelimiter() {
		if (buffer.length() != 0) {
			tokens.add(buffer.toString());
			buffer = new StringBuilder();
		}
	}

	/**
	 * Zanalyzuje URL relativní cestu
	 * 
	 * @param path
	 * 
	 */
	public URLPathAnalyzer(String path) {
		for (char c : path.toCharArray()) {
			if (c == '/') {
				applyDelimiter();
				continue;
			} else {
				buffer.append(c);
			}
		}
		applyDelimiter();
	}

	public boolean isEmpty() {
		return tokens.isEmpty();
	}

	/**
	 * Získá token z cesty dle zadané pozice, nebo vrátí null
	 */
	private String getPathToken(int index) {
		return tokens.size() >= index + 1 ? tokens.get(index) : null;
	}

	/**
	 * Získá token z aktuální pozice
	 */
	public String getCurrentPathToken() {
		return getPathToken(currentIndex);
	}

	/**
	 * Získá token z aktuální pozice, pokud není null posune se jinak jen vrátí
	 * null
	 */
	public String getNextPathToken() {
		String token = getCurrentPathToken();
		if (token == null)
			return null;
		shift();
		return token;
	}

	/**
	 * Postupně jak se parsuje path, je aktuální jiná část, tento index označuje
	 * kterou část cesty by si měla stránky přednostně parsovat - například
	 * stránka na adrese settings/app bude v rootu (UI) naparsována na úrovni 0
	 * ("settings") a přesměrována na settings stránku - tam už je ale podstatná
	 * část s "app" a proto je v UI analyzer posunut na index 1, který bude na
	 * "app" token odkazovat rovnou.
	 */
	public void shift() {
		currentIndex++;
	}

}
