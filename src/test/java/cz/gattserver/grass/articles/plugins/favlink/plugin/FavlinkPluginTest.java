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
		assertEquals("Odkaz", to.description());
		assertEquals("[A]", to.prefix());
		assertEquals("[/A]", to.suffix());
		assertEquals("A", to.tag());
		assertEquals("HTML", to.tagFamily());
	}

}
