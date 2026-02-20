package cz.gattserver.grass.articles.plugins.basic.table;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class HTablePluginTest {

	@Test
	public void testProperties() {
		HTablePlugin plugin = new HTablePlugin();
		assertEquals("HTABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[HTABLE]", to.prefix());
		assertEquals("[/HTABLE]", to.suffix());
		assertEquals("HTABLE", to.tag());
		assertEquals("HTML", to.tagFamily());
	}

}
