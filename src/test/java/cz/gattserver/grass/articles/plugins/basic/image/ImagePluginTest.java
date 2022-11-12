package cz.gattserver.grass.articles.plugins.basic.image;

import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
public class ImagePluginTest {

	@Test
	public void testProperties() {
		ImagePlugin plugin = new ImagePlugin();
		assertEquals("IMG", plugin.getTag());
		assertEquals(ImageParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertNull(to.getDescription());
		assertEquals("[IMG]", to.getPrefix());
		assertEquals("[/IMG]", to.getSuffix());
		assertEquals("IMG", to.getTag());
		assertEquals("HTML", to.getTagFamily());
	}

}
