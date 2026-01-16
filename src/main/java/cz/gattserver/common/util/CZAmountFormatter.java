package cz.gattserver.common.util;

/**
 * Formátovač určený pro přidávání českých formátů dle počtu věci. Řeší
 * tvarování popisků -- například:
 * <ul>
 * <li>1 kostka</li>
 * <li>3 kostky</li>
 * <li>8 kostek</li>
 * </ul>
 * 
 * @author Hynek
 *
 */
public class CZAmountFormatter {

	private static final String DEFAULT_NULL_VALUE = "-";

	private String format1;
	private String format2to4;
	private String format5AndMore;
	private String nullValue;

	/**
	 * 
	 * @param format1
	 *            formát pro počet 1
	 * @param format2to4
	 *            formát pro počty 2 až 4
	 * @param format5AndMore
	 *            formát pro počty 5 a více
	 */
	public CZAmountFormatter(String format1, String format2to4, String format5AndMore) {
		this(format1, format2to4, format5AndMore, DEFAULT_NULL_VALUE);
	}

	/**
	 * 
	 * @param format1
	 *            formát pro počet 1
	 * @param format2to4
	 *            formát pro počty 2 až 4
	 * @param format5AndMore
	 *            formát pro počty 5 a více
	 * @param nullValue
	 *            hodnota pro výpis pro případ 0 a <code>null</code>
	 */
	public CZAmountFormatter(String format1, String format2to4, String format5AndMore, String nullValue) {
		this.format1 = format1;
		this.format2to4 = format2to4;
		this.format5AndMore = format5AndMore;
		this.nullValue = nullValue;
	}

	/**
	 * Zformátuje číslo dle zadaných formátů a nullValue
	 * 
	 * @param number
	 *            číslo na zformátování
	 * @return číslo jako text se formátem
	 */
	public String format(Integer number) {
		if (number == null)
			return nullValue;
		switch (number) {
		case 0:
			return nullValue;
		case 1:
			return String.format(format1, number);
		case 2:
		case 3:
		case 4:
			return String.format(format2to4, number);
		default:
			return String.format(format5AndMore, number);
		}
	}

}
