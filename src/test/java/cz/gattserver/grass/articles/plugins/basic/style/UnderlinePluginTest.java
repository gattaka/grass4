package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UnderlinePluginTest {

    @Test
    public void testProperties() {
        UnderlinePlugin plugin = new UnderlinePlugin();
        assertEquals("UND", plugin.getTag());
        assertTrue(plugin.getParser() instanceof AbstractStyleParser);
        EditorButtonResourcesTO to = plugin.getEditorButtonResources();
        assertNull(to.description());
        assertEquals("[UND]", to.prefix());
        assertEquals("[/UND]", to.suffix());
        assertEquals("UND", to.tag());
        assertEquals("Formátování", to.tagFamily());
    }

}
