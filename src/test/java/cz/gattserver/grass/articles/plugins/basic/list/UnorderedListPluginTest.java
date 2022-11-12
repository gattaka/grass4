package cz.gattserver.grass.articles.plugins.basic.list;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class UnorderedListPluginTest {

	@Test
	public void testProperties() {
		OrderedListPlugin plugin = new OrderedListPlugin();
		assertEquals("OL", plugin.getTag());
		assertEquals(ListParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[OL]", to.getPrefix());
		assertEquals("[/OL]", to.getSuffix());
		assertEquals("OL", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
