package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class SubPluginTest {

	@Test
	public void testProperties() {
		SubPlugin plugin = new SubPlugin();
		assertEquals("SUB", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Sub", to.description());
		assertNull(to.imagePath());
		assertEquals("[SUB]", to.prefix());
		assertEquals("[/SUB]", to.suffix());
		assertEquals("SUB", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
