package cz.gattserver.common.util;

import java.util.Calendar;
import java.util.Date;

public final class DateUtils {

	private DateUtils() {
	}

	public static Date resetTime(Date date) {
		return org.apache.commons.lang3.time.DateUtils.truncate(date, Calendar.DATE);
	}

	public static Date resetTimeToMidnight(Date date) {
		return org.apache.commons.lang3.time.DateUtils.addSeconds(org.apache.commons.lang3.time.DateUtils
				.addMinutes(org.apache.commons.lang3.time.DateUtils.addHours(resetTime(date), 23), 59), 59);
	}
}