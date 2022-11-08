package cz.gattserver.grass.articles.editor.parser.elements;

import cz.gattserver.grass.articles.editor.parser.Context;

import java.util.List;

public class PluginErrorElement implements Element {

	private String text;

	public PluginErrorElement(String pluginName) {
		String prefix = "<span style=\"color: red; border: red dashed 1px; padding: 2px 5px; margin: 2px; font-family: monospace; font-size: 12px; font-weight: normal; font-style: normal; font-variant: normal; background: #ffcc33;\">Plugin '";
		String suffix = "' throws exception</span>";
		text = prefix + pluginName + suffix;
	}

	@Override
	public void apply(Context ctx) {
		ctx.println(text);
	}

	@Override
	public List<Element> getSubElements() {
		return null;
	}
}
