package cz.gattserver.common.util;

public class HumanBytesSizeFormatter {

	private HumanBytesSizeFormatter() {
	}

	/**
	 * Vytvoří popis velikosti souboru v lidsky čitelných hodnotách (kB, MB ...)
	 * 
	 * @param bytes
	 *            velikost jež se zpracovává
	 * @param si
	 *            mají být velikosti počítány jako si (kB ~ 1000) nebo jako
	 *            binární (kiB ~ 1024)?
	 * @return řetězec s popisem velikosti souboru
	 */
	// http://stackoverflow.com/questions/3758606/how-to-convert-byte-size-into-human-readable-format-in-java
	public static String format(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	/**
	 * Vytvoří popis velikosti souboru v lidsky čitelných hodnotách (kB, MB ...)
	 * 
	 * @param bytes
	 *            velikost jež se zpracovává
	 * @return řetězec s popisem velikosti souboru
	 */
	public static String format(long bytes) {
		return HumanBytesSizeFormatter.format(bytes, true);
	}

}
