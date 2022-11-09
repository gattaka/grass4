package cz.gattserver.grass.articles.plugins.style.color;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass3.articles.plugins.basic.style.AbstractStyleParser;
import org.junit.Test;

import static org.junit.Assert.*;

public class RedPluginTest {

	@Test
	public void testProperties() {
		RedPlugin plugin = new RedPlugin();
		assertEquals("RED", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Červeně", to.getDescription());
		assertNull(to.getImage());
		assertEquals("[RED]", to.getPrefix());
		assertEquals("[/RED]", to.getSuffix());
		assertEquals("RED", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
