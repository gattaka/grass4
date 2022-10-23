package cz.gattserver.grass.core.services;

public interface RandomSourceService {

	/**
	 * Získá náhodné long číslo z rozsahu <0-range)
	 * 
	 * @param range
	 *            rozsah, ze kterého se bude vybírat
	 * @return náhodné číslo
	 */
	long getRandomLong(long range);

	/**
	 * Získá náhodné int číslo z rozsahu <0-range)
	 * 
	 * @param range
	 *            rozsah, ze kterého se bude vybírat
	 * @return náhodné číslo
	 */
	int getRandomInt(int range);
}
