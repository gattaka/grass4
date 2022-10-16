package cz.gattserver.grass.ui.js;

public class JScriptItem {

	private String script;
	private boolean plain;

	/**
	 * Vytvoří nový javaScript pro registraci do nahrávací kaskády
	 * 
	 * @param script
	 *            jméno souboru se skriptem
	 */
	public JScriptItem(String script) {
		this(script, false);
	}

	/**
	 * Vytvoří nový javaScript pro registraci do nahrávací kaskády
	 * 
	 * @param script
	 *            javascript skript, nebo jméno souboru se skriptem
	 * @param plain
	 *            {@code true} pokud jde přímo o skript, jinak (pokud jde o
	 *            soubor) {@code false}
	 */
	public JScriptItem(String script, boolean plain) {
		this.script = script;
		this.plain = plain;
	}

	public String getScript() {
		return script;
	}

	public boolean isPlain() {
		return plain;
	}

	@Override
	public String toString() {
		return (plain ? "-SKRIPT-" : script) + " (" + (plain ? "plain" : "file") + ")";
	}

}
