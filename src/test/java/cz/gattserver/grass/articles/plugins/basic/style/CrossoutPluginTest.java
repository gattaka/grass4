package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CrossoutPluginTest {

    @Test
    public void testProperties() {
        CrossoutPlugin plugin = new CrossoutPlugin();
        assertEquals("CROSS", plugin.getTag());
        assertTrue(plugin.getParser() instanceof AbstractStyleParser);
        EditorButtonResourcesTO to = plugin.getEditorButtonResources();
        assertNull(to.description());
        assertEquals("[CROSS]", to.prefix());
        assertEquals("[/CROSS]", to.suffix());
        assertEquals("CROSS", to.tag());
        assertEquals("Formátování", to.tagFamily());
    }

}
