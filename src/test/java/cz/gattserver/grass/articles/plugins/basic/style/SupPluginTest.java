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
		assertEquals("Sup", to.getDescription());
		assertNull(to.getImagePath());
		assertEquals("[SUP]", to.getPrefix());
		assertEquals("[/SUP]", to.getSuffix());
		assertEquals("SUP", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
