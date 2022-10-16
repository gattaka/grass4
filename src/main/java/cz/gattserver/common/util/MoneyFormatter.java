package cz.gattserver.common.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class MoneyFormatter {

	private static NumberFormat priceFormat;

	private MoneyFormatter() {
	}

	static {
		priceFormat = NumberFormat.getCurrencyInstance(new Locale("cs", "CZ"));
		priceFormat.setMinimumFractionDigits(2);
	}

	public static String format(BigDecimal price) {
		return priceFormat.format(price.doubleValue());
	}

	public static String format(double price) {
		return priceFormat.format(price);
	}

}
