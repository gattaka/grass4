package cz.gattserver.grass.articles.plugins.jslibs.numbers;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import cz.gattserver.grass.articles.plugins.jslibs.GJSLibParser;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class GJSLibNumbersPlugin implements Plugin {

	private static final String TAG = "GJSLibNumbers";
	private static final String IMAGE_PATH = "jslibs/img/numbers_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new GJSLibParser(TAG) {
			@Override
			protected Element createElement() {
				return new GJSLibNumbersElement();
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "JS Libs").setPrefix("[" + TAG + "]").setSuffix("[/" + TAG + "]")
				.setDescription("Numbers").setImage(IMAGE_PATH).build();
	}
}
