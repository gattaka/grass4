package cz.gattserver.grass.articles.plugins.basic.table;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TablePluginTest {

	@Test
	public void testProperties() {
		TablePlugin plugin = new TablePlugin();
		assertEquals("TABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.description());
		assertEquals("[TABLE]", to.prefix());
		assertEquals("[/TABLE]", to.suffix());
		assertEquals("TABLE", to.tag());
		assertEquals("HTML", to.tagFamily());
	}

}
