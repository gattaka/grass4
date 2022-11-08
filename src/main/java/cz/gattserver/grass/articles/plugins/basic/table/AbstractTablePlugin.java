package cz.gattserver.grass.articles.plugins.basic.table;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;

/**
 * 
 * @author gatt
 */
public abstract class AbstractTablePlugin implements Plugin {

	private static final String WITH_HEAD_TAG = "HTABLE";
	private static final String WITH_HEAD_IMAGE = "basic/img/htbl_16.png";
	private static final String WITHOUT_HEAD_TAG = "TABLE";
	private static final String WITHOUT_HEAD_IMAGE = "basic/img/tbl_16.png";

	private boolean withHead;

	private String tag;
	private String image;

	public AbstractTablePlugin(boolean withHead) {
		this.withHead = withHead;
		if (withHead) {
			tag = WITH_HEAD_TAG;
			image = WITH_HEAD_IMAGE;
		} else {
			tag = WITHOUT_HEAD_TAG;
			image = WITHOUT_HEAD_IMAGE;
		}
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new TableParser(tag, withHead);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "HTML").setImageAsThemeResource(image).build();
	}

}
