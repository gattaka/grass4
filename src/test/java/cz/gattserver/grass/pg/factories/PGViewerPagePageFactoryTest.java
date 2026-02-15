package cz.gattserver.grass.pg.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.pg.ui.pages.factories.PGViewerPageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PGViewerPagePageFactoryTest {

	@Autowired
	@Qualifier("pgViewerPageFactory")
	private PageFactory pageFactory;

	@Test
	public void testPGEditorPageFactory() {
		assertTrue(pageFactory instanceof PGViewerPageFactory);
		PGViewerPageFactory factory = (PGViewerPageFactory) pageFactory;

		assertEquals("photogallery", factory.getPageName());
	}

}
