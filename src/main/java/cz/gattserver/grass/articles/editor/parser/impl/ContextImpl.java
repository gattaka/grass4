package cz.gattserver.grass.articles.editor.parser.impl;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Kontext pouzity pri generovani kodu.
 */
public class ContextImpl implements Context {

	private static final String END_DIV = "</div>";

	/**
	 * Vystupni stream.
	 */
	private StringBuilder out;

	/**
	 * úroveň text z pohledu nadpisů 0 .. nenastaveno - výchozí
	 */
	private int textLevel = 0;

	/**
	 * Pořadové číslo nadpisu - aby se dal obsah upravovat po částech
	 */
	private int headerIdentifier = 0;

	/**
	 * Dodatečné zdroj vyžadované pluginy
	 */
	private Set<String> cssResources = new LinkedHashSet<>();
	private Set<String> jsResources = new LinkedHashSet<>();
	private Set<String> jsCodes = new LinkedHashSet<>();

	public ContextImpl() {
		this.out = new StringBuilder();
	}

	@Override
	public void print(String s) {
		out.append(s);
	}

	@Override
	public void println(String s) {
		out.append(s);
	}

	@Override
	public void setHeaderLevel(int level) {
		// pokud byla již úroveň změněná, nejprve
		// uzavři předchozí odsazovací div
		if (this.textLevel != 0)
			out.append(END_DIV);

		// ulož si pro příští porovnání aktuální level
		textLevel = level;

		// vlož odsazovací div
		out.append("<div class=\"level").append(level).append("\">");
	}

	@Override
	public String getOutput() {
		// pokud byla úroveň odsazení změněná, uzavři odsazovací div
		// Zde by se mohl sice textLevel rovnou vynulovat, ale já připouštím
		// možnost získat výstup a pak pokračovat ve vypisování výstupu
		return (this.textLevel != 0) ? (out.toString() + END_DIV) : out.toString();
	}

	@Override
	public int getNextHeaderIdentifier() {
		return headerIdentifier++;
	}

	@Override
	public void resetHeaderLevel() {
		// pokud byla úroveň opravdu změněná,
		// uzavři předchozí odsazovací div
		if (this.textLevel != 0) {
			out.append(END_DIV);
			textLevel = 0;
		}
	}

	@Override
	public void addCSSResource(String url) {
		cssResources.add(url);
	}

	@Override
	public void addJSResource(String url) {
		jsResources.add(url);
	}

	@Override
	public void addJSCode(String code) {
		jsCodes.add(code);
	}

	@Override
	public Set<String> getCSSResources() {
		return cssResources;
	}

	@Override
	public Set<String> getJSResources() {
		return jsResources;
	}

	@Override
	public Set<String> getJSCodes() {
		return jsCodes;
	}
}
