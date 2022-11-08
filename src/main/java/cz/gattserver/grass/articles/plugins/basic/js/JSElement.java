package cz.gattserver.grass.articles.plugins.basic.js;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class JSElement implements Element {

	private String content;

	public JSElement(String content) {
		this.content = content;
	}

	@Override
	public void apply(Context ctx) {
		ctx.addJSCode(content);
	}
	
	@Override
	public List<Element> getSubElements() {
		return null;
	}

}
