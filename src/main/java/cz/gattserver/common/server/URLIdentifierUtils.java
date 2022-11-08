package cz.gattserver.common.server;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class URLIdentifierUtils {

	private URLIdentifierUtils() {
	}

	public static class URLIdentifier {
		private String name;
		private Long id;

		private URLIdentifier(Long id, String name) {
			this.id = id;
			this.name = name;
		}

		public Long getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	private static char transformChars(char c) {
		switch (c) {
		case 'á':
			return 'a';
		case 'č':
			return 'c';
		case 'ď':
			return 'd';
		case 'é':
			return 'e';
		case 'ě':
			return 'e';
		case 'í':
			return 'i';
		case 'ň':
			return 'n';
		case 'ó':
			return 'o';
		case 'ř':
			return 'r';
		case 'š':
			return 's';
		case 'ť':
			return 't';
		case 'ú':
			return 'u';
		case 'ů':
			return 'u';
		case 'ý':
			return 'y';
		case 'ž':
			return 'z';
		case ' ':
			return '-';
		default:
			if ((c + "").matches("[0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]"))
				return c;
			return '-';
		}
	}

	/**
	 * <p>
	 * Vytvoří URL identifikátor ve tvaru
	 * </p>
	 * 
	 * <pre>
	 * ID - Název
	 * </pre>
	 * <p>
	 * tedy například
	 * </p>
	 * 
	 * <pre>
	 * 21 - Software
	 * </pre>
	 * 
	 * @param id
	 *            číselný identifikátor
	 * @param name
	 *            jmenný identifikátor
	 * @return URL identifikátor kategorie
	 */
	public static String createURLIdentifier(Long id, String name) {
		try {
			StringBuilder sb = new StringBuilder();
			name = name.toLowerCase();
			for (int i = 0; i < name.length(); i++) {
				char c = transformChars(name.charAt(i));
				if (c != 0)
					sb.append(c);
			}
			name = sb.toString().replaceAll("[-]+", "-");

			String identifier = URLEncoder.encode(String.valueOf(id) + "-" + name, "UTF-8");
			// Tomcat má default nastavené ignorovat adresy ve kterých je %2F
			// https://www.assembla.com/spaces/liftweb/wiki/Tomcat/print
			// http://forum.spring.io/forum/spring-projects/web/97212-url-encoded-in-pathvariable-value-causes-problems
			// Nově to Spring security už vůbec nepovoluje
			// https://stackoverflow.com/questions/48580584/stricthttpfirewall-in-spring-security-4-2-vs-spring-mvc-matrixvariable
			return identifier.replaceAll("%2F", "").replaceAll("%3B", "");
		} catch (UnsupportedEncodingException e) {
			// UTF-8 missing - vážně ?
			throw new IllegalStateException("Nezdařilo se vytvoření URL identifikátoru", e);
		}
	}

	/**
	 * Naparsuje URL identifikátor a vrátí jeho položky v novém
	 * {@link URLIdentifier} objektu
	 * 
	 * @param identifier
	 *            {@link String} identifikátor
	 * @return {@link URLIdentifier} objekt s identifikačními údaji, nebo
	 *         {@code null} pokud nejsou splněny
	 */
	public static URLIdentifier parseURLIdentifier(String identifier) {
		if (identifier == null)
			return null;

		// získej ID
		String[] parts = identifier.split("-");
		if (parts.length <= 1)
			return null;

		Long id = null;
		try {
			id = Long.valueOf(parts[0]);
		} catch (NumberFormatException e) {
			return null;
		}

		try {
			String name = URLDecoder.decode(parts[1], "UTF-8");
			return new URLIdentifier(id, name);
		} catch (UnsupportedEncodingException e) {
			// UTF-8 missing - vážně ?
			throw new IllegalStateException("Nezdařilo se naparsovat URL identifikátor", e);
		}
	}
}
