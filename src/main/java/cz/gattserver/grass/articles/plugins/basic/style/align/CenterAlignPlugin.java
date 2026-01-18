package cz.gattserver.grass.articles.plugins.basic.style.align;

import com.vaadin.flow.component.html.Image;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleElement;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStylePlugin;
import cz.gattserver.grass.articles.editor.parser.Parser;
import cz.gattserver.grass.articles.editor.parser.elements.Element;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTOBuilder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gatt
 */
@Component
public class CenterAlignPlugin extends AbstractStylePlugin {

    public static final String TAG = "ALGNCT";

    public CenterAlignPlugin() {
        super(TAG);
    }

    @Override
    public Parser getParser() {
        return new AbstractAlignParser(TAG) {

            @Override
            protected AbstractStyleElement getElement(List<Element> elist) {
                return new CenterAlignElement(elist);
            }
        };
    }

    @Override
    public EditorButtonResourcesTO getEditorButtonResources() {
        return new EditorButtonResourcesTOBuilder(TAG, AbstractStylePlugin.PLUGIN_FAMILY).setImage(
                "basic/img/algnc_16.png").build();
    }
}