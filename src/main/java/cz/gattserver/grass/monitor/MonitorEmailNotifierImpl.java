package cz.gattserver.grass.monitor;

import java.util.TimerTask;

import cz.gattserver.grass.core.services.MailService;
import cz.gattserver.grass.monitor.processor.item.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.monitor.services.MonitorService;

@Slf4j
@Component
public class MonitorEmailNotifierImpl extends TimerTask implements MonitorEmailNotifier {

    private final MonitorService monitorService;
    private final MailService mailService;

    public MonitorEmailNotifierImpl(MonitorService monitorService, MailService mailService) {
        this.monitorService = monitorService;
        this.mailService = mailService;
    }

    @Override
    public void run() {
        log.info("MonitorEmailNotifier byl spuštěn");

        int notificationsSend = 0;

        // Test, zda jsou nahozené systémy serveru
        for (URLMonitorItemTO to : monitorService.getServersStatus().getItems()) {
            if (!MonitorState.SUCCESS.equals(to.getMonitorState())) {
                mailService.sendToAdmin("GRASS Monitor oznámení o změně stavu monitorovaného předmětu",
                        "Server služba " + to.getName() + " není aktivní nebo se nezdařilo zjistit její stav");
                notificationsSend++;
            }
        }

        // Test, zda je připojen backup disk
        BackupStatusPartItemTO backupStatusPartItemTO = monitorService.getBackupStatus();
        if (!MonitorState.SUCCESS.equals(backupStatusPartItemTO.getMonitorState())) {
            mailService.sendToAdmin("GRASS Monitor oznámení o změně stavu monitorovaného předmětu",
                    "Backup disk není připojen nebo se nezdařilo zjistit jeho stav");
            notificationsSend++;

            // Test, zda jsou prováděny pravidelně zálohy
            for (BackupStatusMonitorItemTO to : backupStatusPartItemTO.getItems()) {
                if (!MonitorState.SUCCESS.equals(to.getMonitorState())) {
                    mailService.sendToAdmin("GRASS Monitor oznámení o změně stavu monitorovaného předmětu",
                            to.getValue() +
                                    " Záloha nebyla provedena, je starší než 24h nebo se nezdařilo zjistit její stav");
                    notificationsSend++;
                }
            }
        }

        // Test, zda jsou disky dle SMART v pořádku
        for (SMARTMonitorItemTO to : monitorService.getSMARTInfo().getItems()) {
            if (MonitorState.ERROR.equals(to.getMonitorState())) {
                mailService.sendToAdmin("GRASS Monitor oznámení o změně stavu monitorovaného předmětu",
                        "Nezdařilo se zjistit stav SMART monitoru: " + to.getStateDetails());
                notificationsSend++;
                break;
            }
            if (MonitorState.ERROR.equals(to.getMonitorState())) {
                mailService.sendToAdmin("GRASS Monitor oznámení o změně stavu monitorovaného předmětu",
                        "SMART monitor detekoval chyby");
                notificationsSend++;
                break;
            }
        }

        log.info("MonitorEmailNotifier doběhl -- celkem zasláno chybových oznámení: " + notificationsSend);
    }

    @Override
    public TimerTask getTimerTask() {
        return this;
    }

}
