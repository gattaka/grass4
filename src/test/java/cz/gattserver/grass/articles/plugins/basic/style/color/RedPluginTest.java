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
		assertEquals("Červeně", to.getDescription());
		assertNull(to.getImagePath());
		assertEquals("[RED]", to.getPrefix());
		assertEquals("[/RED]", to.getSuffix());
		assertEquals("RED", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
