package cz.gattserver.grass.articles.plugins.basic.style.color;

import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleParser;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStylePlugin;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * @author gatt
 */
@Component
public class RedPlugin extends AbstractStylePlugin {

	public static final String TAG = "RED";

	public RedPlugin() {
		super(TAG);
	}

	@Override
	public Parser getParser() {
		return new AbstractStyleParser(TAG) {

			@Override
			protected AbstractStyleElement getElement(List<Element> elist) {
				return new RedElement(elist);
			}
		};
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, AbstractStylePlugin.PLUGIN_FAMILY).setDescription("Červeně")
				.build();
	}

}
