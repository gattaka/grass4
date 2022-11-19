package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class SystemSwapMonitorItemTO extends MonitorItemTO {

	private long total;
	private long used;
	private long free;

	public SystemSwapMonitorItemTO() {
	}

	public SystemSwapMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		if (monitorState != MonitorState.SUCCESS)
			return;
		total = (long) jsonObject.getNumber("total");
		used = (long) jsonObject.getNumber("used");
		free = (long) jsonObject.getNumber("free");
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getUsed() {
		return used;
	}

	public void setUsed(long used) {
		this.used = used;
	}

	public long getFree() {
		return free;
	}

	public void setFree(long free) {
		this.free = free;
	}

	public float getUsedRation() {
		return (float) used / total;
	}

}
