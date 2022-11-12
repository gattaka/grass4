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
        assertNull(to.getDescription());
        assertEquals("[CROSS]", to.getPrefix());
        assertEquals("[/CROSS]", to.getSuffix());
        assertEquals("CROSS", to.getTag());
        assertEquals("Formátování", to.getTagFamily());
    }

}
