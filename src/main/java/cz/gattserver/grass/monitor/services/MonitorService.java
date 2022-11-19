package cz.gattserver.grass.monitor.services;

import cz.gattserver.grass.monitor.config.MonitorConfiguration;
import cz.gattserver.grass.monitor.processor.item.DiskStatusPartItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMMemoryMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMPIDMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMThreadsMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.JVMUptimeMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.BackupStatusPartItemTO;
import cz.gattserver.grass.monitor.processor.item.SMARTPartItemTO;
import cz.gattserver.grass.monitor.processor.item.ServersPartItemTO;
import cz.gattserver.grass.monitor.processor.item.ServicesPartItemTO;
import cz.gattserver.grass.monitor.processor.item.SystemMemoryMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.SystemSwapMonitorItemTO;
import cz.gattserver.grass.monitor.processor.item.SystemUptimeMonitorItemTO;

public interface MonitorService {

	MonitorConfiguration getConfiguration();

	void storeConfiguration(MonitorConfiguration configuration);

	SystemUptimeMonitorItemTO getSystemUptime();

	SystemMemoryMonitorItemTO getSystemMemoryStatus();

	SystemSwapMonitorItemTO getSystemSwapStatus();

	BackupStatusPartItemTO getBackupStatus();

	DiskStatusPartItemTO getDiskStatus();

	ServersPartItemTO getServersStatus();

	JVMUptimeMonitorItemTO getJVMUptime();

	JVMThreadsMonitorItemTO getJVMThreads();

	JVMMemoryMonitorItemTO getJVMMemory();

	JVMPIDMonitorItemTO getJVMPID();

	SMARTPartItemTO getSMARTInfo();

	ServicesPartItemTO getServicesStatus();
}
