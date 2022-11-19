package cz.gattserver.grass.medic;


import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import cz.gattserver.grass.medic.interfaces.ScheduledVisitTO;
import cz.gattserver.grass.medic.util.MedicUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MedicUtilTest {

	@Test
	public void test() throws ParseException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
		ScheduledVisitTO dto = new ScheduledVisitTO();

		String dateStart = "21.12.2012 09:29:58";
		String dateStop = "02.01.2013 10:31:48";

		dto.setDateTime(LocalDateTime.parse(dateStop, formatter));
		assertFalse(MedicUtil.fromNowAfter7Days(dto, LocalDateTime.parse(dateStart, formatter)));

		dateStart = "26.12.2012 09:29:58";
		dateStop = "02.01.2013 10:31:48";

		dto.setDateTime(LocalDateTime.parse(dateStop, formatter));
		assertTrue(MedicUtil.fromNowAfter7Days(dto, LocalDateTime.parse(dateStart, formatter)));

	}
}