package cz.gattserver.grass.articles.plugins.basic.html;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;

/**
 * @author gatt
 */
public class HTMLPlugin implements Plugin {

	private static final String TAG = "HTML";
	private static final String DESCRIPTION = "HTML";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new HTMLParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setDescription(DESCRIPTION)
				.setImageAsThemeResource("basic/img/htmlxml_16.png").build();
	}
}
