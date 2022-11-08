package cz.gattserver.grass.articles.plugins.favlink.plugin;


import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FavlinkPluginTest  {

	@Test
	public void testProperties() {
		FavlinkPlugin plugin = new FavlinkPlugin();
		assertEquals("A", plugin.getTag());
		assertEquals(FavlinkParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Odkaz", to.getDescription());
		assertEquals("[A]", to.getPrefix());
		assertEquals("[/A]", to.getSuffix());
		assertEquals("A", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
