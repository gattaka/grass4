package cz.gattserver.grass.monitor;

import java.util.TimerTask;

import cz.gattserver.grass.core.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cz.gattserver.grass.monitor.processor.item.BackupStatusMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.BackupStatusPartItemTO;
import cz.gattserver.grass.monitor.processor.item.MonitorState;
import cz.gattserver.grass.monitor.processor.item.SMARTMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.ServersMonitorItemTO;
import cz.gattserver.grass.monitor.services.MonitorService;

@Component
public class MonitorEmailNotifierImpl extends TimerTask implements MonitorEmailNotifier {

	private static Logger logger = LoggerFactory.getLogger(MonitorEmailNotifierImpl.class);

	@Autowired
	private MonitorService monitorFacade;

	@Autowired
	private MailService mailService;

	@Override
	public void run() {
		logger.info("Monitor TimerTask byl spuštěn");

		// Test, zda jsou nahozené systémy serveru
		for (ServersMonitorItemTO to : monitorFacade.getServersStatus().getItems()) {
			if (!MonitorState.SUCCESS.equals(to.getMonitorState()))
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"Server služba " + to.getName() + " není aktivní nebo se nezdařilo zjistit její stav");
		}

		// Test, zda je připojen backup disk
		BackupStatusPartItemTO backupStatusPartItemTO = monitorFacade.getBackupStatus();
		if (!MonitorState.SUCCESS.equals(backupStatusPartItemTO.getMonitorState())) {
			mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
					"Backup disk není připojen nebo se nezdařilo zjistit jeho stav");

			// Test, zda jsou prováděny pravidelně zálohy
			for (BackupStatusMonitorItemTO to : backupStatusPartItemTO.getItems()) {
				if (!MonitorState.SUCCESS.equals(to.getMonitorState()))
					mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu", to
							.getValue()
							+ " Záloha nebyla provedena, je starší než 24h nebo se nezdařilo zjistit její stav");
			}
		}

		// Test, zda jsou disky dle SMART v pořádku
		for (SMARTMonitorItemTO to : monitorFacade.getSMARTInfo().getItems()) {
			if (MonitorState.UNAVAILABLE.equals(to.getMonitorState())) {
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"Nezdařilo se zjistit stav SMART monitoru: " + to.getStateDetails());
				break;
			}
			if (MonitorState.ERROR.equals(to.getMonitorState())) {
				mailService.sendToAdmin("GRASS3 Monitor oznámení o změně stavu monitorovaného předmětu",
						"SMART monitor detekoval chyby");
				break;
			}
		}
	}

	@Override
	public TimerTask getTimerTask() {
		return this;
	}

}
