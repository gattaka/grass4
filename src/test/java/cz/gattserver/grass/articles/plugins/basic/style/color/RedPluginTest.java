package cz.gattserver.grass.articles.plugins.basic.style.color;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.basic.style.AbstractStyleParser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RedPluginTest {

	@Test
	public void testProperties() {
		RedPlugin plugin = new RedPlugin();
		assertEquals("RED", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Červeně", to.description());
		assertNull(to.imagePath());
		assertEquals("[RED]", to.prefix());
		assertEquals("[/RED]", to.suffix());
		assertEquals("RED", to.tag());
		assertEquals("Formátování", to.tagFamily());
	}

}
