package cz.gattserver.grass.articles.plugins.templates;


import cz.gattserver.grass.articles.editor.parser.interfaces.EditorButtonResourcesTO;
import cz.gattserver.grass.articles.plugins.templates.plotter.PlotterParser;
import cz.gattserver.grass.articles.plugins.templates.plotter.PlotterPlugin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlotterPluginTest {

	@Test
	public void testProperties() {
		PlotterPlugin plugin = new PlotterPlugin();
		assertEquals("PLOTTER", plugin.getTag());
		assertEquals(PlotterParser.class, plugin.getParser().getClass());
		EditorButtonResourcesTO to = plugin.getEditorButtonResources();
		assertEquals("Plotter", to.getDescription());
		assertEquals("[PLOTTER]x*x;2;5;0;0[;width][;height]", to.getPrefix());
		assertEquals("[/PLOTTER]", to.getSuffix());
		assertEquals("PLOTTER", to.getTag());
		assertEquals("Šablony", to.getTagFamily());
	}

}
