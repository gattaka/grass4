package cz.gattserver.grass.pg.factories;

import cz.gattserver.grass.core.ui.pages.factories.template.PageFactory;
import cz.gattserver.grass.pg.ui.pages.factories.PGEditorPageFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PGEditorPageFactoryTest {

	@Resource
	@Qualifier("pgEditorPageFactory")
	private PageFactory pageFactory;

	@Test
	public void testPGEditorPageFactory() {
		assertTrue(pageFactory instanceof PGEditorPageFactory);
		PGEditorPageFactory factory = (PGEditorPageFactory) pageFactory;

		assertEquals("pg-editor", factory.getPageName());
	}

}
