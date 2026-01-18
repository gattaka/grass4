package cz.gattserver.grass.articles.plugins.templates.sources;

import cz.gattserver.common.vaadin.ImageIcon;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import cz.gattserver.grass.articles.plugins.Plugin;
import cz.gattserver.grass.articles.plugins.favlink.strategies.CombinedFaviconObtainStrategy;
import org.springframework.stereotype.Component;

/**
 * @author gatt
 */
@Component
public class LinksPlugin implements Plugin {

    private static final String TAG = "LINKS";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Parser getParser() {
        return new SourcesParser(TAG, new CombinedFaviconObtainStrategy(), false, false);
    }

    @Override
    public EditorButtonResourcesTO getEditorButtonResources() {
        return new EditorButtonResourcesTOBuilder(TAG, "Šablony").setDescription("Odkazy")
                .setImage(ImageIcon.GLOBE_16_ICON.createImage()).build();
    }
}