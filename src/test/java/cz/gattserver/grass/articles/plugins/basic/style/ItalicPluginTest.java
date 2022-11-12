package cz.gattserver.grass.articles.plugins.basic.style;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class ItalicPluginTest {

	@Test
	public void testProperties() {
		ItalicPlugin plugin = new ItalicPlugin();
		assertEquals("EM", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[EM]", to.getPrefix());
		assertEquals("[/EM]", to.getSuffix());
		assertEquals("EM", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
