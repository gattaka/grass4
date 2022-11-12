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
		assertNull(to.getDescription());
		assertEquals("[MONSPC]", to.getPrefix());
		assertEquals("[/MONSPC]", to.getSuffix());
		assertEquals("MONSPC", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
