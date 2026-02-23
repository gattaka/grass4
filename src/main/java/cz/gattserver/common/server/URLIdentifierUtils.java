package cz.gattserver.common.server;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URLIdentifierUtils {

	private URLIdentifierUtils() {
	}

    public record URLIdentifier(Long id, String name) {
    }

	private static char transformChars(char c) {
        return switch (c) {
            case 'á' -> 'a';
            case 'č' -> 'c';
            case 'ď' -> 'd';
            case 'é' -> 'e';
            case 'ě' -> 'e';
            case 'í' -> 'i';
            case 'ň' -> 'n';
            case 'ó' -> 'o';
            case 'ř' -> 'r';
            case 'š' -> 's';
            case 'ť' -> 't';
            case 'ú' -> 'u';
            case 'ů' -> 'u';
            case 'ý' -> 'y';
            case 'ž' -> 'z';
            case ' ' -> '-';
            default -> {
                if ((c + "").matches("[0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ]")) yield c;
                yield '-';
            }
        };
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
        StringBuilder sb = new StringBuilder();
        name = name.toLowerCase();
        for (int i = 0; i < name.length(); i++) {
            char c = transformChars(name.charAt(i));
            if (c != 0)
                sb.append(c);
        }
        name = sb.toString().replaceAll("[-]+", "-");

        String identifier = URLEncoder.encode(id + "-" + name, StandardCharsets.UTF_8);
        // Tomcat má default nastavené ignorovat adresy ve kterých je %2F
        // https://www.assembla.com/spaces/liftweb/wiki/Tomcat/print
        // http://forum.spring.io/forum/spring-projects/web/97212-url-encoded-in-pathvariable-value-causes-problems
        // Nově to Spring security už vůbec nepovoluje
        // https://stackoverflow.com/questions/48580584/stricthttpfirewall-in-spring-security-4-2-vs-spring-mvc-matrixvariable
        return identifier.replaceAll("%2F", "").replaceAll("%3B", "");
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

		long id;
		try {
			id = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return null;
		}

        String name = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
        return new URLIdentifier(id, name);
    }
}
