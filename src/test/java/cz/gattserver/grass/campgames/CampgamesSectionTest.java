package cz.gattserver.grass.campgames;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;

import cz.gattserver.grass.campgames.ui.pages.factories.CampgamesPageFactory;
import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CampgamesSectionTest {

	@Autowired
	@Qualifier("campgamesSection")
	private SectionService sectionService;

	@Test
	public void testFMSection() {
		assertTrue(sectionService instanceof CampgamesSection);
		assertEquals("Hry", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof CampgamesPageFactory);
		assertTrue(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.USER))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR))));
	}

}
