package cz.gattserver.grass.articles.plugins.basic.style.color;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GreenPluginTest {

	@Test
	public void testProperties() {
		GreenPlugin plugin = new GreenPlugin();
		assertEquals("GRN", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Zeleně", to.description());
		assertNull(to.imagePath());
		assertEquals("[GRN]", to.prefix());
		assertEquals("[/GRN]", to.suffix());
		assertEquals("GRN", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
