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
		assertNull(to.description());
		assertEquals("[STR]", to.prefix());
		assertEquals("[/STR]", to.suffix());
		assertEquals("STR", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
