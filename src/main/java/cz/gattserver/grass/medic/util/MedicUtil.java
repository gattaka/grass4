package cz.gattserver.grass.medic.util;

import java.time.LocalDateTime;

public class MedicUtil {

    private MedicUtil() {
    }

    public static boolean isVisitPending(LocalDateTime date) {
        LocalDateTime now = LocalDateTime.now();
        return date.getMonthValue() == now.getMonthValue() && date.getYear() == now.getYear();
    }

    public static boolean fromNowAfter7Days(LocalDateTime date, LocalDateTime now) {
        return now.toLocalDate().plusDays(7).isEqual(date.toLocalDate());
    }

}
