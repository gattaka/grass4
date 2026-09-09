package cz.gattserver.common.server;

import jakarta.annotation.Nullable;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class URLIdentifierUtils {

    private URLIdentifierUtils() {
    }

    public record URLIdentifier(Long id, String name, String hash) {
    }

    private static char transformChars(char c) {
        return switch (c) {
            case 'á' -> 'a';
            case 'č' -> 'c';
            case 'ď' -> 'd';
            case 'é', 'ě' -> 'e';
            case 'í' -> 'i';
            case 'ň' -> 'n';
            case 'ó' -> 'o';
            case 'ř' -> 'r';
            case 'š' -> 's';
            case 'ť' -> 't';
            case 'ú', 'ů' -> 'u';
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
     * @param id   číselný identifikátor
     * @param name jmenný identifikátor
     * @return URL identifikátor kategorie
     */
    public static String createURLIdentifier(Long id, String name) {
        return createURLIdentifier(id, name, null);
    }

    public static String createURLIdentifier(Long id, String name, @Nullable String explicitAccessHash) {
        StringBuilder sb = new StringBuilder();
        name = name.toLowerCase();
        for (int i = 0; i < name.length(); i++) {
            char c = transformChars(name.charAt(i));
            if (c != 0) sb.append(c);
        }
        name = sb.toString().replaceAll("[-]+", "-");

        String value = id + "-" + name;
        if (explicitAccessHash != null) value = value + "-" + explicitAccessHash;

        String identifier = URLEncoder.encode(value, StandardCharsets.UTF_8);
        // Tomcat má default nastavené ignorovat adresy ve kterých je %2F
        // https://www.assembla.com/spaces/liftweb/wiki/Tomcat/print
        // http://forum.spring.io/forum/spring-projects/web/97212-url-encoded-in-pathvariable-value-causes-problems
        // Nově to Spring security už vůbec nepovoluje
        // https://stackoverflow.com/questions/48580584/stricthttpfirewall-in-spring-security-4-2-vs-spring-mvc-matrixvariable
        return identifier.replace("%2F", "").replace("%3B", "");
    }


    /**
     * Naparsuje URL identifikátor a vrátí jeho položky v novém
     * {@link URLIdentifier} objektu
     *
     * @param identifier {@link String} identifikátor
     * @return {@link URLIdentifier} objekt s identifikačními údaji, nebo
     * {@code null} pokud nejsou splněny
     */
    public static URLIdentifier parseURLIdentifier(String identifier) {
        if (identifier == null) return null;

        // získej ID
        String[] parts = identifier.split("-");
        if (parts.length <= 1) return null;

        long id;
        try {
            id = Long.parseLong(parts[0]);
        } catch (NumberFormatException e) {
            return null;
        }

        String name = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);

        // hash může obsahovat '-', takže parsování dle částí dál už nedává smysl
        String hash = null;
        if (parts.length > 2 && identifier.lastIndexOf("-") != identifier.length() - 1) {
            String tryHash = identifier.substring(identifier.lastIndexOf("-") + 1);
            // pokud text není 43 znaků dlouhý, není to určitě kontrolní hash
            if (tryHash.length() == 43) hash = tryHash;
        }

        return new URLIdentifier(id, name, hash);
    }
}
