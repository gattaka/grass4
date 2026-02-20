package cz.gattserver.grass.articles.plugins.basic.style.align;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CenterAlignPluginTest {

	@Test
	public void testProperties() {
		CenterAlignPlugin plugin = new CenterAlignPlugin();
		assertEquals("ALGNCT", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractAlignParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[ALGNCT]", to.prefix());
		assertEquals("[/ALGNCT]", to.suffix());
		assertEquals("ALGNCT", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
