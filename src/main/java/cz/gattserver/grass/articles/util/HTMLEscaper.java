package cz.gattserver.grass.articles.util;

/**
 * Provádí převod HTML znaků, ze kterých jsou stavěny HTML tagy na bezpečné
 * escapované řetězce, které budou mít efekt pouze jako text
 * 
 * @author Hynek
 *
 */
public class HTMLEscaper {

	private HTMLEscaper() {
	}

	/**
	 * Provádí escapování HTML znaků, ze kterých jsou stavěny HTML tagy
	 * 
	 * @param htmlUnsafeText
	 *            text s HTML tagy, který má být převeden
	 * @return převedený bezpečný text, ve kterém jsou HTML tagy převedeny na
	 *         text
	 */
	public static String stringToHTMLString(String htmlUnsafeText) {
		StringBuilder sb = new StringBuilder(htmlUnsafeText.length());
		int len = htmlUnsafeText.length();
		char c;

		for (int i = 0; i < len; i++) {
			c = htmlUnsafeText.charAt(i);
			switch (c) {
			case '"':
				sb.append("&quot;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			default:
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
