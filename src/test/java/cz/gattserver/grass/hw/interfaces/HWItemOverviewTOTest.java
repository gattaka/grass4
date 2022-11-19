package cz.gattserver.grass.hw.interfaces;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class HWItemOverviewTOTest {

	@Test
	public void testEquals() {
		HWItemOverviewTO to = new HWItemOverviewTO();
		to.setId(1L);
		to.setName("Name1");

		HWItemOverviewTO to2 = new HWItemOverviewTO();
		to2.setId(1L);
		to2.setName("Name2");

		assertTrue(to.equals(to2));
		assertTrue(to2.equals(to));
	}

	@Test
	public void testEquals2() {
		HWItemOverviewTO to = new HWItemOverviewTO();
		to.setName("Name1");

		HWItemOverviewTO to2 = new HWItemOverviewTO();
		to2.setName("Name2");

		assertFalse(to.equals(to2));
		assertFalse(to2.equals(to));
	}

	@Test
	public void testEquals3() {
		// Pro jistotu jsou equals jenom dle == nebo id
		HWItemOverviewTO to = new HWItemOverviewTO();
		to.setName("Name");

		HWItemOverviewTO to2 = new HWItemOverviewTO();
		to2.setName("Name");

		assertFalse(to.equals(to2));
		assertFalse(to2.equals(to));
	}

	@Test
	public void testEquals4() {
		HWItemOverviewTO to = new HWItemOverviewTO();
		to.setName("Name");

		assertTrue(to.equals(to));
	}

}
