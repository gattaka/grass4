package cz.gattserver.grass.medic.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;

public class MedicUtil {

	private MedicUtil() {
	}

	public static boolean isVisitPending(ScheduledVisitTO dto) {
		LocalDateTime date = dto.getDateTime();
		LocalDateTime now = LocalDateTime.now();
		return date.getMonthValue() == now.getMonthValue() && date.getYear() == now.getYear();
	}

	public static boolean fromNowAfter7Days(ScheduledVisitTO dto, LocalDateTime now) {
		return now.plusDays(7).truncatedTo(ChronoUnit.DAYS).isEqual(dto.getDateTime().truncatedTo(ChronoUnit.DAYS));
	}

}
