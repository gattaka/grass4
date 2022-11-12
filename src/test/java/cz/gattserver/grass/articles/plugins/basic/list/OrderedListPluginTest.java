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
		assertNull(to.getDescription());
		assertEquals("[UL]", to.getPrefix());
		assertEquals("[/UL]", to.getSuffix());
		assertEquals("UL", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
