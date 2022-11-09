package cz.gattserver.grass.articles.plugins.style;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnderlinePluginTest {

	@Test
	public void testProperties() {
		UnderlinePlugin plugin = new UnderlinePlugin();
		assertEquals("UND", plugin.getTag());
		assertTrue(plugin.getParser() instanceof AbstractStyleParser);
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[UND]", to.getPrefix());
		assertEquals("[/UND]", to.getSuffix());
		assertEquals("UND", to.getTag());
		assertEquals("Formátování", to.getTagFamily());
	}

}
