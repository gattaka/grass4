package cz.gattserver.grass.fm.factories;

import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.util.DBCleanTest;
import cz.gattserver.grass.fm.web.factories.FMSettingsPageFactory;
import cz.gattserver.grass.test.MockSecurityService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FMSettingsPageFactoryTest extends DBCleanTest {

	@Autowired
	private FMSettingsPageFactory pageFactory;

	@Autowired
	private MockSecurityService mockSecurityService;

	@Test
	public void testFMSettingsPageFactory() {
		assertTrue(pageFactory instanceof FMSettingsPageFactory);
		FMSettingsPageFactory factory = (FMSettingsPageFactory) pageFactory;

		assertEquals("Soubory", factory.getSettingsCaption());
		assertEquals("fm", factory.getSettingsURL());

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
