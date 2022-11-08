package cz.gattserver.grass.articles.plugins.code;

import cz.gattserver.grass.articles.editor.parser.Context;
import cz.gattserver.grass.articles.editor.parser.elements.Element;

import java.util.List;

public class CodeElement implements Element {

	private String code;
	private String description;
	private String lib;
	private String mimetype;

	public CodeElement(String code, String description, String lib, String mimetype) {
		this.code = code;
		this.description = description;
		this.lib = lib;
		this.mimetype = mimetype;
	}

	@Override
	public void apply(Context ctx) {

		// CSS resources
		ctx.addCSSResource("articles/code/code_style.css");
		ctx.addCSSResource("articles/code/lib/codemirror.css");

		// JS resources
		ctx.addJSResource("articles/code/lib/codemirror.js");
		ctx.addJSResource("articles/code/addon/edit/matchbrackets.js");
		ctx.addJSResource("articles/code/addon/fold/xml-fold.js");
		ctx.addJSResource("articles/code/addon/edit/matchtags.js");
		ctx.addJSResource("articles/code/addon/selection/active-line.js");
		// je potřeba například pro Docker
		ctx.addJSResource("articles/code/addon/mode/simple.js");  

		if (lib != null)
			ctx.addJSResource("articles/code/mode/" + lib + "/" + lib + ".js");
		
		ctx.addJSResource("articles/code/lib/codemirror_scan.js");
 
		ctx.print("<span class=\"lang_description\">" + description + "</span>");
		ctx.print("<div class=\"barier\"><div class=\"numberedtext\">");
		ctx.print("<textarea codemirror mimetype=\"" + mimetype + "\">" + code + "</textarea>");
		ctx.print("</div></div><div id=\"code_koncovka\"></div>");
	}

	@Override
	public String toString() {
		return "Code highlight";
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}

}
