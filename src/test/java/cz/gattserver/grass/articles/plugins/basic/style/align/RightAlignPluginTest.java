package cz.gattserver.grass.articles.plugins.basic.style.align;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RightAlignPluginTest {

	@Test
	public void testProperties() {
		RightAlignPlugin plugin = new RightAlignPlugin();
		assertEquals("ALGNRT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractAlignParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[ALGNRT]", to.prefix());
		assertEquals("[/ALGNRT]", to.suffix());
		assertEquals("ALGNRT", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
