package cz.gattserver.grass.articles.plugins.basic.table;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.basic.table.TableParser;
import cz.gattserver.grass.articles.plugins.basic.table.TablePlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TablePluginTest {

	@Test
	public void testProperties() {
		TablePlugin plugin = new TablePlugin();
		assertEquals("TABLE", plugin.getTag());
		assertEquals(TableParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[TABLE]", to.getPrefix());
		assertEquals("[/TABLE]", to.getSuffix());
		assertEquals("TABLE", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
