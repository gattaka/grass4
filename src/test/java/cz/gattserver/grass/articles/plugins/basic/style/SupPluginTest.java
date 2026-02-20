package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class SupPluginTest {

	@Test
	public void testProperties() {
		SupPlugin plugin = new SupPlugin();
		assertEquals("SUP", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Sup", to.description());
		assertNull(to.imagePath());
		assertEquals("[SUP]", to.prefix());
		assertEquals("[/SUP]", to.suffix());
		assertEquals("SUP", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
