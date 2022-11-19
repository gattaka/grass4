package cz.gattserver.grass.medic;

import cz.gattserver.grass.medic.interfaces.ScheduledVisitState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class StateTest {

	@Test
	public void test() {

		assertTrue(ScheduledVisitState.MISSED
				.compareTo(ScheduledVisitState.PLANNED) > 0);

		assertTrue(ScheduledVisitState.MISSED
				.compareTo(ScheduledVisitState.TO_BE_PLANNED) > 0);

		assertTrue(ScheduledVisitState.TO_BE_PLANNED
				.compareTo(ScheduledVisitState.PLANNED) > 0);

	}

}
