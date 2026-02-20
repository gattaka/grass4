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
		assertNull(to.description());
		assertEquals("[EM]", to.prefix());
		assertEquals("[/EM]", to.suffix());
		assertEquals("EM", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
