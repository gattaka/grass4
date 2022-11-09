package cz.gattserver.grass.articles.plugins.style.color;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class BluePluginTest {

	@Test
	public void testProperties() {
		BluePlugin plugin = new BluePlugin();
		assertEquals("BLU", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Modře", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[BLU]", to.getPrefix());
		assertEquals("[/BLU]", to.getSuffix());
		assertEquals("BLU", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
