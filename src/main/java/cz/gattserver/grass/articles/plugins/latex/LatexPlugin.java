package cz.gattserver.grass.articles.plugins.latex;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class LatexPlugin implements Plugin {

	private final String tag = "TEX";

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new LatexParser(tag);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(tag, "LaTeX").build();
	}

}
