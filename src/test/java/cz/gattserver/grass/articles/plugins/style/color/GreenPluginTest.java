package cz.gattserver.grass.articles.plugins.style.color;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class GreenPluginTest {

	@Test
	public void testProperties() {
		GreenPlugin plugin = new GreenPlugin();
		assertEquals("GRN", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Zeleně", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[GRN]", to.getPrefix());
		assertEquals("[/GRN]", to.getSuffix());
		assertEquals("GRN", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
