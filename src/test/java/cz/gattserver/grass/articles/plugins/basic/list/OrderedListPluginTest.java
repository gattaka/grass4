package cz.gattserver.grass.articles.plugins.basic.list;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
public class OrderedListPluginTest {

	@Test
	public void testProperties() {
		UnorderedListPlugin plugin = new UnorderedListPlugin();
		assertEquals("UL", plugin.getTag());
		assertEquals(ListParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[UL]", to.prefix());
		assertEquals("[/UL]", to.suffix());
		assertEquals("UL", to.tag());
		assertEquals("HTML", to.tagFamily());
	}

}
