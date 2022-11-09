package cz.gattserver.grass.articles.plugins.table;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class HTablePluginTest {

	@Test
	public void testProperties() {
		HTablePlugin plugin = new HTablePlugin();
		assertEquals("HTABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[HTABLE]", to.getPrefix());
		assertEquals("[/HTABLE]", to.getSuffix());
		assertEquals("HTABLE", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
