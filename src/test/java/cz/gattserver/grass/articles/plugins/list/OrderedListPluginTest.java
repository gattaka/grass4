package cz.gattserver.grass.articles.plugins.list;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class OrderedListPluginTest {

	@Test
	public void testProperties() {
		UnorderedListPlugin plugin = new UnorderedListPlugin();
		assertEquals("UL", plugin.getTag());
		assertEquals(ListParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[UL]", to.getPrefix());
		assertEquals("[/UL]", to.getSuffix());
		assertEquals("UL", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
