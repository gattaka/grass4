package cz.gattserver.grass.hw;


import java.util.Arrays;
import java.util.HashSet;

import cz.gattserver.grass.core.modules.SectionService;
import cz.gattserver.grass.core.security.CoreRole;
import cz.gattserver.grass.core.util.DBCleanTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import cz.gattserver.grass.hw.ui.pages.factories.HWPageFactory;

public class HWSectionTest extends DBCleanTest {

	@Autowired
	@Qualifier("hwSection")
	private SectionService sectionService;

	@Test
	public void testHWSection() {
		assertTrue(sectionService instanceof HWSection);
		assertEquals("HW", sectionService.getSectionCaption());
		assertTrue(sectionService.getSectionPageFactory() instanceof HWPageFactory);
		assertTrue(sectionService.isVisibleForRoles(null));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.ADMIN))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.FRIEND))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.USER))));
		assertTrue(sectionService.isVisibleForRoles(new HashSet<>(Arrays.asList(CoreRole.AUTHOR))));
	}

}
