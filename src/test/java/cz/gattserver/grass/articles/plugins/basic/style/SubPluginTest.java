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
		assertEquals("Sub", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[SUB]", to.getPrefix());
		assertEquals("[/SUB]", to.getSuffix());
		assertEquals("SUB", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
