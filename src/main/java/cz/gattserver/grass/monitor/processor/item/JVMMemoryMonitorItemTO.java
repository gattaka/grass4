package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class JVMMemoryMonitorItemTO extends MonitorItemTO {

	private long usedMemory;
	private long freeMemory;
	private long maxMemory;
	private long totalMemory;

	public JVMMemoryMonitorItemTO() {
	}

	public JVMMemoryMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		usedMemory = (long) jsonObject.getNumber("usedMemory");
		freeMemory = (long) jsonObject.getNumber("freeMemory");
		maxMemory = (long) jsonObject.getNumber("maxMemory");
		totalMemory = (long) jsonObject.getNumber("totalMemory");
	}

	public long getUsedMemory() {
		return usedMemory;
	}

	public void setUsedMemory(long usedMemory) {
		this.usedMemory = usedMemory;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

}
