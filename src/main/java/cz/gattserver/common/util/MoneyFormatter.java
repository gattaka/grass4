package cz.gattserver.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormatter {

    private static NumberFormat priceFormat;

    private MoneyFormatter() {
    }

    static {
        priceFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("cs-CZ"));
        priceFormat.setMinimumFractionDigits(2);
    }

    /**
     * Formátuje číslo jako cenu. Od Java 13 je nutné pro testy brát v potaz,
     * že mezery jsou pro cs-CZ nově formátováním zapisovány jako NBSP (\u00a02).
     * Toto je potřeba zohlednit především v testech.
     *
     * @param price cena
     * @return
     */
    public static String format(BigDecimal price) {
        return priceFormat.format(price.doubleValue());
    }

    public static String format(double price) {
        return priceFormat.format(price);
    }

}
