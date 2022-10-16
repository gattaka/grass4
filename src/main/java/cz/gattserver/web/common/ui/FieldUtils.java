package cz.gattserver.web.common.ui;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class FieldUtils {

	private FieldUtils() {
	}

	public static String formatMoney(BigDecimal money) {
		if (money == null)
			return null;
		NumberFormat priceFormat = NumberFormat.getCurrencyInstance(new Locale("cs", "CZ"));
		((DecimalFormat) priceFormat).setMaximumFractionDigits(2);
		((DecimalFormat) priceFormat).setMinimumFractionDigits(2);
		return priceFormat.format(money);
	}
}