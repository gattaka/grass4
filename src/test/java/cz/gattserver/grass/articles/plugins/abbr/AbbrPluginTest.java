package cz.gattserver.grass.articles.plugins.abbr;

import cz.gattserver.grass3.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.Test;

import static org.junit.Assert.*;

public class AbbrPluginTest {

	@Test
	public void testProperties() {
		AbbrPlugin plugin = new AbbrPlugin();
		assertEquals("ABBR", plugin.getTag());
		assertEquals(AbbrParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[ABBR]", to.getPrefix());
		assertEquals("[T][/T][/ABBR]", to.getSuffix());
		assertEquals("ABBR", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
