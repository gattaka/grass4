package cz.gattserver.grass.articles.plugins.basic.list;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;

/**
 * @author gatt
 */
public abstract class AbstractListPlugin implements Plugin {

	private final boolean ordered;
	private final String tag;
	private final String image;

	public AbstractListPlugin(String tag, String image, boolean ordered) {
		this.tag = tag;
		this.image = image;
		this.ordered = ordered;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new ListParser(tag, ordered);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "HTML").setImageAsThemeResource(image).build();
	}
}
