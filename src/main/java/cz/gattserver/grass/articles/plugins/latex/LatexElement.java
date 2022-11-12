package cz.gattserver.grass.articles.plugins.latex;


import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;
import java.util.UUID;

public class LatexElement implements Element {

	private String formula;

	public LatexElement(String formula) {
		this.formula = formula;
	}

	@Override
	public void apply(Context ctx) {
		// CSS resources
		ctx.addCSSResource("articles/katex/katex.min.css");

		// JS resources
		ctx.addJSResource("articles/katex/katex.min.js");

		String uuid = "katex" + UUID.randomUUID().toString();
		ctx.print("<span id=\"" + uuid + "\"></span>");

		String katexFormula = formula.replace("\\", "\\\\");
		ctx.addJSCode("katex.render(\"" + katexFormula + "\", document.getElementById(\"" + uuid
				+ "\"), {throwOnError: false});");
	}

	@Override
	public String toString() {
		return String.format("LaTeX %s", formula);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
