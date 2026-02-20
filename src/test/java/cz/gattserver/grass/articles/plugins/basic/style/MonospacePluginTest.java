package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class MonospacePluginTest {

	@Test
	public void testProperties() {
		MonospacePlugin plugin = new MonospacePlugin();
		assertEquals("MONSPC", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[MONSPC]", to.prefix());
		assertEquals("[/MONSPC]", to.suffix());
		assertEquals("MONSPC", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
