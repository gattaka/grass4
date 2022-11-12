package cz.gattserver.grass.articles.plugins.templates.sort;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class SortPlugin implements Plugin {

	private static final String TAG = "SORT";
	private static final String IMAGE_PATH = "templates/img/sort_16.png";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new SortParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setDescription("Řazení")
				.setImageAsThemeResource(IMAGE_PATH).build();
	}
}
