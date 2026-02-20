package cz.gattserver.grass.articles.plugins.basic.style.align;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LeftAlignPluginTest {

	@Test
	public void testProperties() {
		LeftAlignPlugin plugin = new LeftAlignPlugin();
		assertEquals("ALGNLT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractAlignParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[ALGNLT]", to.prefix());
		assertEquals("[/ALGNLT]", to.suffix());
		assertEquals("ALGNLT", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
