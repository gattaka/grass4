package cz.gattserver.grass.articles.plugins.jslibs;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class JSLibElement implements Element {

	private String scriptPath;

	public JSLibElement(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addJSResource(scriptPath);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
