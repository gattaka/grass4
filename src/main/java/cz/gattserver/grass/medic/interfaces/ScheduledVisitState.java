package cz.gattserver.grass.medic.interfaces;

/**
 * Stavy plánované návštěvy
 */
public enum ScheduledVisitState implements Comparable<ScheduledVisitState> {

	/**
	 * Objednána
	 */
	PLANNED("Objednán"),

	/**
	 * Plánovaná k objednání
	 */
	TO_BE_PLANNED("K objednání"),

	/**
	 * Zmeškána - přeobjednat
	 */
	MISSED("Zmeškáno");

	private String localized;

	private ScheduledVisitState(String localized) {
		this.localized = localized;
	}

	@Override
	public String toString() {
		return localized;
	}
}
