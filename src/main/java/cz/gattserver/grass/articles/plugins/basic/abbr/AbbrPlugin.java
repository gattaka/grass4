package cz.gattserver.grass.articles.plugins.basic.abbr;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class AbbrPlugin implements Plugin {

	private static final String TAG = "ABBR";
	private static final String TITLE_TAG = "T";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new AbbrParser(TAG, TITLE_TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setPrefix("[" + TAG + "]")
				.setSuffix("[" + TITLE_TAG + "][/" + TITLE_TAG + "][/" + TAG + "]")
				.setImage("basic/img/abbr_16.png").build();
	}
}
