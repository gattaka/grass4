package cz.gattserver.grass.articles.plugins.basic.style.color;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BluePluginTest {

	@Test
	public void testProperties() {
		BluePlugin plugin = new BluePlugin();
		assertEquals("BLU", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Modře", to.description());
		assertNull(to.imagePath());
		assertEquals("[BLU]", to.prefix());
		assertEquals("[/BLU]", to.suffix());
		assertEquals("BLU", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
