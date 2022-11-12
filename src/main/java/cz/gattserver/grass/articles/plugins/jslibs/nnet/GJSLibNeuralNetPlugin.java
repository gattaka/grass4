package cz.gattserver.grass.articles.plugins.jslibs.nnet;

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
public class GJSLibNeuralNetPlugin implements Plugin {

	private static final String TAG = "GJSLibNeuralNet";
	private static final String IMAGE_PATH = "jslibs/img/nnet_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new GJSLibParser(TAG) {
			@Override
			protected Element createElement() {
				return new GJSLibNeuralNetElement();
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "JS Libs").setPrefix("[" + TAG + "]").setSuffix("[/" + TAG + "]")
				.setDescription("NNet").setImageAsThemeResource(IMAGE_PATH).build();
	}
}
