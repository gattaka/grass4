package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class StrongPluginTest {

	@Test
	public void testProperties() {
		StrongPlugin plugin = new StrongPlugin();
		assertEquals("STR", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[STR]", to.getPrefix());
		assertEquals("[/STR]", to.getSuffix());
		assertEquals("STR", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
