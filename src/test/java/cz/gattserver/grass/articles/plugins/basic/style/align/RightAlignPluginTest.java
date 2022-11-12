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
		assertNull(to.getDescription());
		assertEquals("[ALGNRT]", to.getPrefix());
		assertEquals("[/ALGNRT]", to.getSuffix());
		assertEquals("ALGNRT", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
