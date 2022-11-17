package cz.gattserver.grass.pg.factories;

import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.pg.ui.pages.factories.PGSettingsPageFactory;
import cz.gattserver.grass.test.MockSecurityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PGSettingsPageFactoryTest {

	@Autowired
	private PGSettingsPageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testPGSettingsPageFactory() {
		assertTrue(pageFactory instanceof PGSettingsPageFactory);
		PGSettingsPageFactory factory = (PGSettingsPageFactory) pageFactory;

		assertEquals("Fotogalerie", factory.getSettingsCaption());
		assertEquals("photogallery", factory.getSettingsURL());

		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN)));
		assertTrue(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR)));
		assertFalse(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.USER)));
		assertFalse(factory.isAuthorized());
		mockSecurityService.setRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND)));
		assertFalse(factory.isAuthorized());
	}

}
