package cz.gattserver.grass.monitor.services.impl;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.gattserver.grass.core.services.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cz.gattserver.grass.monitor.config.MonitorConfiguration;
import cz.gattserver.grass.monitor.processor.Console;
import cz.gattserver.grass.monitor.processor.ConsoleOutputTO;
import cz.gattserver.grass.monitor.processor.item.DiskStatusMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.DiskStatusPartItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.BackupStatusMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.BackupStatusPartItemTO;
import cz.gattserver.grass.monitor.processor.item.MonitorState;
import cz.gattserver.grass.monitor.processor.item.SMARTMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.SMARTPartItemTO;
import cz.gattserver.grass.monitor.processor.item.ServersMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.ServersPartItemTO;
import cz.gattserver.grass.monitor.processor.item.ServicesMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.ServicesPartItemTO;
import cz.gattserver.grass.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.SystemUptimeMonitorItemTO;
import cz.gattserver.grass.monitor.services.MonitorService;

@Transactional
@Component
public class MonitorServiceImpl implements MonitorService {

	private static final Logger logger = LoggerFactory.getLogger(MonitorServiceImpl.class);

	private static final int HTTP_TEST_TIMEOUT = 5000;

	@Autowired
	private ConfigurationService configurationService;

	@Override
	public MonitorConfiguration getConfiguration() {
		MonitorConfiguration configuration = new MonitorConfiguration();
		configurationService.loadConfiguration(configuration);
		return configuration;
	}

	@Override
	public void storeConfiguration(MonitorConfiguration configuration) {
		configurationService.saveConfiguration(configuration);
	}

	private ConsoleOutputTO runScript(String script, String... param) {
		String scriptsDir = getConfiguration().getScriptsDir();
		List<String> items = new ArrayList<>();
		items.add(scriptsDir + "/" + script + ".sh");
		for (String p : param)
			items.add(p);
		return Console.executeCommand(items);
	}

	@Override
	public SystemUptimeMonitorItemTO getSystemUptime() {
		ConsoleOutputTO to = Console.executeCommand("uptime");
		SystemUptimeMonitorItemTO uptimeTO = new SystemUptimeMonitorItemTO();
		uptimeTO.setMonitorState(to.isSuccess() ? MonitorState.SUCCESS : MonitorState.UNAVAILABLE);
		uptimeTO.setValue(to.getOutput());
		return uptimeTO;
	}

	@Override
	public SystemMemoryMonitorItemTO getSystemMemoryStatus() {
		// #!/bin/bash
		// free | grep 'Mem' | grep -o [0-9]*
		ConsoleOutputTO to = runScript("getMemoryStatus");
		SystemMemoryMonitorItemTO itemTO = new SystemMemoryMonitorItemTO();

		String[] values = to.getOutput().split("\n");
		if (values.length != 6) {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			return itemTO;
		} else {
			try {
				// * 1000 protože údaje jsou v KB
				itemTO.setTotal(Long.parseLong(values[0]) * 1000);
				itemTO.setUsed(Long.parseLong(values[1]) * 1000);
				itemTO.setFree(Long.parseLong(values[2]) * 1000);
				itemTO.setShared(Long.parseLong(values[3]) * 1000);
				itemTO.setBuffCache(Long.parseLong(values[4]) * 1000);
				itemTO.setAvailable(Long.parseLong(values[5]) * 1000);
			} catch (NumberFormatException e) {
				itemTO.setMonitorState(MonitorState.UNAVAILABLE);
				return itemTO;
			}
		}

		itemTO.setMonitorState(MonitorState.SUCCESS);
		return itemTO;
	}

	@Override
	public SystemSwapMonitorItemTO getSystemSwapStatus() {
		// #!/bin/bash
		// free | grep 'Swap' | grep -o [0-9]*
		ConsoleOutputTO to = runScript("getSwapStatus");
		SystemSwapMonitorItemTO itemTO = new SystemSwapMonitorItemTO();

		String[] values = to.getOutput().split("\n");
		if (values.length != 3) {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			return itemTO;
		} else {
			try {
				// * 1000 protože údaje jsou v KB
				itemTO.setTotal(Long.parseLong(values[0]) * 1000);
				itemTO.setUsed(Long.parseLong(values[1]) * 1000);
				itemTO.setFree(Long.parseLong(values[2]) * 1000);
			} catch (NumberFormatException e) {
				logger.error("Zpracování výstupu z getSystemSwapStatus se nezdařilo", e);
				itemTO.setMonitorState(MonitorState.UNAVAILABLE);
				return itemTO;
			}
		}

		itemTO.setMonitorState(MonitorState.SUCCESS);
		return itemTO;
	}

	@Override
	public BackupStatusPartItemTO getBackupStatus() {
		BackupStatusPartItemTO mainItemTO = new BackupStatusPartItemTO();

		// #!/bin/bash
		//
		// disk=$( df -h | grep backup )
		//
		// if [ -z "$disk" ]
		// then echo "false"
		// else echo "true"
		// fi
		ConsoleOutputTO to = runScript("isBackupDiskMounted");
		mainItemTO.setValue(to.getOutput());
		if (to.isSuccess()) {
			mainItemTO
					.setMonitorState(Boolean.parseBoolean(to.getOutput()) ? MonitorState.SUCCESS : MonitorState.ERROR);
		} else {
			mainItemTO.setMonitorState(MonitorState.UNAVAILABLE);
		}

		// #!/bin/bash
		// echo -n "SRV "
		// tail -n 1 /mnt/backup/srv-backup.log
		// echo -n "SYS "
		// tail -n 1 /mnt/backup/srv-systemctl-backup.log
		// echo -n "FTP "
		// tail -n 1 /mnt/backup/ftp-backup.log
		to = runScript("getLastTimeOfBackup");
		List<BackupStatusMonitorItemTO> list = new ArrayList<>();

		String dummTarget = "TTT Last backup:  ";
		String dummyDate = "HH:MM:SS DD.MM.YYYY";
		String dummyLog = dummTarget + dummyDate;

		if (to.isSuccess()) {
			for (String part : to.getOutput().split("\n")) {
				BackupStatusMonitorItemTO itemTO = new BackupStatusMonitorItemTO();
				mainItemTO.getItems().add(itemTO);
				if (part.length() == dummyLog.length()) {
					String target = part.substring(0, 3);
					String date = part.substring(dummTarget.length());
					LocalDateTime lastBackup = LocalDateTime.parse(date,
							DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy"));
					itemTO.setValue(target + ": poslední záloha byla provedena " + date);
					// poslední záloha nesmí být starší než 24h
					if (lastBackup.isBefore(LocalDateTime.now().minusHours(24)))
						itemTO.setMonitorState(MonitorState.ERROR);
					else
						itemTO.setMonitorState(MonitorState.SUCCESS);
					itemTO.setLastTime(lastBackup);
				} else {
					// nejsou podklady pro info o poslední záloze? Chyba!
					itemTO.setMonitorState(MonitorState.ERROR);
				}
				list.add(itemTO);
			}
		} else {
			BackupStatusMonitorItemTO itemTO = new BackupStatusMonitorItemTO();
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			list.add(itemTO);
		}
		return mainItemTO;
	}

	@Override
	public DiskStatusPartItemTO getDiskStatus() {
		DiskStatusPartItemTO partItemTO = new DiskStatusPartItemTO();

		// #!/bin/bash
		// /usr/bin/mount | egrep '^/'
		ConsoleOutputTO to = runScript("getDiskMounts");
		if (!to.isSuccess()) {
			return createDiskStatusErrorOutput(to.getOutput());
		} else {
			partItemTO.setMonitorState(MonitorState.SUCCESS);
		}
		String mounts[] = to.getOutput().split("\n");
		Map<String, String> devToMount = new HashMap<>();
		for (String mount : mounts) {
			String info[] = mount.split(" ");
			devToMount.put(info[0], info[2]);
		}

		List<DiskStatusMonitorItemTO> disks = new ArrayList<>();
		for (FileStore store : FileSystems.getDefault().getFileStores()) {
			String mount = devToMount.get(store.name());
			if (mount == null)
				continue;
			DiskStatusMonitorItemTO itemTO = new DiskStatusMonitorItemTO();
			partItemTO.getItems().add(itemTO);
			itemTO.setName(store.name());
			itemTO.setMount(mount);
			try {
				if (!analyzeStore(itemTO, store))
					continue;
				itemTO.setMonitorState(MonitorState.SUCCESS);
			} catch (IOException e) {
				itemTO.setMonitorState(MonitorState.UNAVAILABLE);
			}
			disks.add(itemTO);
		}
		return partItemTO;
	}

	private DiskStatusPartItemTO createDiskStatusErrorOutput(String reason) {
		DiskStatusPartItemTO partItemTO = new DiskStatusPartItemTO();
		DiskStatusMonitorItemTO item = new DiskStatusMonitorItemTO();
		item.setStateDetails(reason);
		item.setMonitorState(MonitorState.UNAVAILABLE);
		partItemTO.getItems().add(item);
		return partItemTO;
	}

	@Override
	public JVMUptimeMonitorItemTO getJVMUptime() {
		JVMUptimeMonitorItemTO to = new JVMUptimeMonitorItemTO();
		try {
			to.setUptime(ManagementFactory.getRuntimeMXBean().getUptime());
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public JVMThreadsMonitorItemTO getJVMThreads() {
		JVMThreadsMonitorItemTO to = new JVMThreadsMonitorItemTO();
		try {
			ThreadMXBean tb = ManagementFactory.getThreadMXBean();
			to.setCount(tb.getThreadCount());
			to.setPeak(tb.getPeakThreadCount());
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public JVMMemoryMonitorItemTO getJVMMemory() {
		JVMMemoryMonitorItemTO to = new JVMMemoryMonitorItemTO();
		try {
			Runtime runtime = Runtime.getRuntime();
			to.setUsedMemory(runtime.totalMemory() - runtime.freeMemory());
			to.setFreeMemory(runtime.freeMemory());
			to.setTotalMemory(runtime.totalMemory());
			to.setMaxMemory(runtime.maxMemory());
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	@Override
	public JVMPIDMonitorItemTO getJVMPID() {
		JVMPIDMonitorItemTO to = new JVMPIDMonitorItemTO();
		try {
			// https://stackoverflow.com/questions/35842/how-can-a-java-program-get-its-own-process-id
			to.setPid(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]);
			to.setMonitorState(MonitorState.SUCCESS);
		} catch (Exception e) {
			to.setMonitorState(MonitorState.UNAVAILABLE);
		}
		return to;
	}

	private boolean analyzeStore(DiskStatusMonitorItemTO to, FileStore store) throws IOException {
		to.setTotal(store.getTotalSpace());
		// pokud je velikost disku 0, pak jde o virtuální skupinu jako
		// proc, sys apod. -- ty stejně nechci zobrazovat
		if (to.getTotal() == 0)
			return false;
		to.setType(store.type());
		to.setUsable(store.getUsableSpace());
		return true;
	}

	@Override
	public ServersPartItemTO getServersStatus() {
		ServersPartItemTO partItemTO = new ServersPartItemTO();

		ServersMonitorItemTO syncthingTO = new ServersMonitorItemTO("Syncthing", "http://gattserver.cz:8127");
		testResponseCode(syncthingTO, true);
		partItemTO.getItems().add(syncthingTO);

		ServersMonitorItemTO nexusTO = new ServersMonitorItemTO("Sonatype Nexus", "https://nexus.gattserver.cz");
		testResponseCode(nexusTO);
		partItemTO.getItems().add(nexusTO);

		ServersMonitorItemTO sonarTO = new ServersMonitorItemTO("SonarQube", "https://sonarqube.gattserver.cz");
		testResponseCode(sonarTO);
		partItemTO.getItems().add(sonarTO);

		return partItemTO;
	}

	private void testResponseCode(ServersMonitorItemTO itemTO) {
		testResponseCode(itemTO, false);
	}

	private void testResponseCode(ServersMonitorItemTO itemTO, boolean anyCode) {
		try {
			URL url = new URL(itemTO.getAddress());
			URLConnection uc = url.openConnection();
			if (uc != null && uc instanceof HttpURLConnection) {
				// HttpURLConnection
				HttpURLConnection hc = (HttpURLConnection) uc;
				hc.setInstanceFollowRedirects(true);

				// bez agenta to často hodí 403 Forbidden, protože si myslí,
				// že jsem asi bot ... (což vlastně jsem)
				hc.setRequestProperty("User-Agent", "Mozilla");
				hc.setConnectTimeout(HTTP_TEST_TIMEOUT);
				hc.setReadTimeout(HTTP_TEST_TIMEOUT);
				hc.connect();
				itemTO.setResponseCode(hc.getResponseCode());
				if (anyCode || itemTO.getResponseCode() >= 200 && itemTO.getResponseCode() < 300)
					itemTO.setMonitorState(MonitorState.SUCCESS);
				else
					itemTO.setMonitorState(MonitorState.ERROR);
			}
		} catch (SocketTimeoutException e) {
			itemTO.setMonitorState(MonitorState.ERROR);
		} catch (Exception e) {
			itemTO.setMonitorState(MonitorState.UNAVAILABLE);
		}
	}

	@Override
	public SMARTPartItemTO getSMARTInfo() {
		final String TIME_HEADER = "SYSLOG_TIMESTAMP";
		final String PRIORITY_HEADER = "PRIORITY";
		final String MESSAGE_HEADER = "MESSAGE";

		SMARTPartItemTO partItemTO = new SMARTPartItemTO();
		// /usr/bin/journalctl -e -u smartd -S 2019-11-03 --lines=10
		// --output-fields=SYSLOG_TIMESTAMP,PRIORITY,MESSAGE -o json
		ConsoleOutputTO out = runScript("getSmartStatus");
		if (out.isSuccess()) {
			try {
				String[] lines = out.getOutput().split("\n");
				for (String line : lines) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNode = mapper.readTree(line);
					String message = jsonNode.get(MESSAGE_HEADER).asText();
					int priority = jsonNode.get(PRIORITY_HEADER).asInt();
					JsonNode timeNode = jsonNode.get(TIME_HEADER);
					String time = timeNode == null ? "-null-" : timeNode.asText();
					SMARTMonitorItemTO to = new SMARTMonitorItemTO(time, message);

					// https://www.freedesktop.org/software/systemd/man/journalctl.html
					switch (priority) {
					case 0: // "emerg" (0)
					case 1: // "alert" (1)
					case 2: // "crit" (2)
					case 3: // "err" (3)
					case 4: // "warning" (4)
						to.setMonitorState(MonitorState.ERROR);
						partItemTO.getItems().add(to);
						break;
					case 5: // "notice" (5)
					case 6: // "info" (6)
					case 7: // "debug" (7)
					default:
						// unused
						break;
					}
				}
				if (partItemTO.getItems().isEmpty()) {
					partItemTO.setMonitorState(MonitorState.SUCCESS);
					partItemTO.setStateDetails("Vše OK");
				}
			} catch (Exception e) {
				logger.error("Zpracování výstupu z getSMARTInfo se nezdařilo", e);
				return createSMARTErrorOutput("Nezdařilo se zpracovat JSON výstup smartd");
			}
		} else {
			return createSMARTErrorOutput("Nezdařilo se získat přehled smartd");
		}
		return partItemTO;
	}

	private SMARTPartItemTO createSMARTErrorOutput(String reason) {
		SMARTPartItemTO partItemTO = new SMARTPartItemTO();
		partItemTO.setStateDetails(reason);
		partItemTO.setMonitorState(MonitorState.UNAVAILABLE);
		return partItemTO;
	}

	@Override
	public ServicesPartItemTO getServicesStatus() {
		ServicesPartItemTO partItemTO = new ServicesPartItemTO();
		partItemTO.setMonitorState(MonitorState.ERROR);
		ConsoleOutputTO out = runScript("getServicesStatus");
		if (out.isSuccess()) {
			try {
				String[] lines = out.getOutput().split("\n");
				if (lines.length < 2)
					return createServicesErrorOutput("Nezdařilo se získat přehled služeb");
				String lastLine = lines[lines.length - 1];
				int units = Integer.valueOf(lastLine.split(" ")[0]);
				for (int i = 0; i < units; i++) {
					String unitLine = lines[1 + i];
					String[] columns = unitLine.split(" ");
					if (columns.length < 6)
						return createServicesErrorOutput("Nezdařilo se zpracovat přehled služeb");
					ServicesMonitorItemTO item = new ServicesMonitorItemTO(columns[1], columns[2], columns[3],
							columns[4], columns[5]);
					item.setMonitorState(MonitorState.ERROR);
					partItemTO.getItems().add(item);
				}
				if (partItemTO.getItems().isEmpty()) {
					partItemTO.setMonitorState(MonitorState.SUCCESS);
					partItemTO.setStateDetails("Vše OK");
				}
			} catch (Exception e) {
				return createServicesErrorOutput("Nezdařilo se zpracovat JSON výstup smartd");
			}
		} else {
			return createServicesErrorOutput("Nezdařilo se získat přehled smartd");
		}
		return partItemTO;
	}

	private ServicesPartItemTO createServicesErrorOutput(String reason) {
		ServicesPartItemTO partItemTO = new ServicesPartItemTO();
		partItemTO.setStateDetails(reason);
		partItemTO.setMonitorState(MonitorState.UNAVAILABLE);
		return partItemTO;
	}

}
