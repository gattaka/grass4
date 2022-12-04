package cz.gattserver.grass.articles.plugins.code;

import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import org.apache.commons.lang3.StringUtils;

/**
 * @author gatt
 */
public abstract class AbstractCodePlugin implements Plugin {

	private final String tag;
	private final String description;
	private final String image;
	private final String lib;
	private final String mimetype;

	public AbstractCodePlugin(String tag, String description, String image, String lib, String mimetype) {
		this.tag = tag;
		this.description = description;
		this.image = image;
		this.lib = lib;
		this.mimetype = mimetype;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public Parser getParser() {
		return new CodeParser(tag, description, lib, mimetype);
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		EditorButtonResourcesTOBuilder builder = new EditorButtonResourcesTOBuilder(tag, "Code highlight")
				.setDescription(description);
		if (StringUtils.isNotBlank(image))
			builder.setImageAsThemeResource("code/img/" + image);
		return builder.build();
	}
}
