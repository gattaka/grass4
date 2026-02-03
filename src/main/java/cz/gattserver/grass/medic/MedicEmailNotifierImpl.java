package cz.gattserver.grass.medic;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

import cz.gattserver.grass.core.services.MailService;
import cz.gattserver.grass.medic.interfaces.ScheduledVisitOverviewTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.medic.service.MedicService;
import cz.gattserver.grass.medic.util.MedicUtil;

@Component
public class MedicEmailNotifierImpl extends TimerTask implements MedicEmailNotifier {

    private static Logger logger = LoggerFactory.getLogger(MedicEmailNotifierImpl.class);

    @Autowired
    private MedicService medicService;

    @Autowired
    private MailService mailService;

    @Override
    public void run() {
        logger.info("Medic TimerTask byl spuštěn");
        for (ScheduledVisitOverviewTO to : medicService.getAllScheduledVisits()) {
            if (MedicUtil.fromNowAfter7Days(to.getDateTime(), LocalDateTime.now())) {
                mailService.sendToAdmin("GRASS Medic oznámená o plánované události", "Událost naplánovaná na: " +
                        to.getDateTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                        " se blíží (nastane za 7 dní):\n\n" + "Instituce:\t" + to.getInstitutionCaption() +
                        "\nDůvod návštěvy:\t" + to.getPurpose());
            }
        }
    }

    @Override
    public TimerTask getTimerTask() {
        return this;
    }

}
