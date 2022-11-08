package cz.gattserver.grass.articles.plugins.favlink.plugin;

import cz.gattserver.grass.articles.plugins.favlink.strategies.CombinedFaviconObtainStrategy;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import cz.gattserver.web.common.ui.ImageIcon;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class FavlinkPlugin implements Plugin {

	private static final String TAG = "A";

	@Override
	public String getTag() {
		return TAG;
	}

	@Override
	public Parser getParser() {
		return new FavlinkParser(TAG, new CombinedFaviconObtainStrategy());
	}

	@Override
	public EditorButtonResourcesTO getEditorButtonResources() {
		return new EditorButtonResourcesTOBuilder(TAG, "HTML").setDescription("Odkaz")
				.setImageResource(ImageIcon.GLOBE_16_ICON.createResource()).build();
	}
}
