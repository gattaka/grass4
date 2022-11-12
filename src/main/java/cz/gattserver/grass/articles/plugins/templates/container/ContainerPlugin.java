package cz.gattserver.grass.articles.plugins.templates.container;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class ContainerPlugin implements Plugin {

	private static final String TAG = "CONTAINER";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new ContainerParser(TAG);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setDescription("Kontejner")
				.setImageResource(ImageIcon.DOWN_16_ICON.createResource()).build();
	}
}
