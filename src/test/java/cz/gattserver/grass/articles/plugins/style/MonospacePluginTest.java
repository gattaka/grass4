package cz.gattserver.grass.articles.plugins.style;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.Test;

import static org.junit.Assert.*;

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
