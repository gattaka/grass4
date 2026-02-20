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
		assertNull(to.description());
		assertEquals("[OL]", to.prefix());
		assertEquals("[/OL]", to.suffix());
		assertEquals("OL", to.tag());
		assertEquals("HTML", to.tagFamily());
	}

}
