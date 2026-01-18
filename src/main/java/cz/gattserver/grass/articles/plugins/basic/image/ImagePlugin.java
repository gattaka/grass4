package cz.gattserver.grass.articles.plugins.basic.image;

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
public class ImagePlugin implements Plugin {

    private static final String TAG = "IMG";

    @Override
    public String getTag() {
        return TAG;
    }

    @Override
    public Parser getParser() {
        return new ImageParser(TAG);
    }

    @Override
    public EditorButtonResourcesTO getEditorButtonResources() {
        return new EditorButtonResourcesTOBuilder(TAG, "HTML").setImage(ImageIcon.IMG_16_ICON.createImage()).build();
    }
}