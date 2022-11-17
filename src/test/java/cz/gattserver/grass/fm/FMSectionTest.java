package cz.gattserver.grass.fm;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.fm.web.factories.FMPageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FMSectionTest {

	@Autowired
	@Qualifier("fmSection")
	private SectionService sectionService;

	@Test
	public void testFMSection() {
		assertTrue(sectionService instanceof FMSection);
		assertEquals("Soubory", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof FMPageFactory);
		assertFalse(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.USER))));
		assertFalse(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR))));
	}

}
