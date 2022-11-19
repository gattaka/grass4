package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class JVMThreadsMonitorItemTO extends MonitorItemTO {

	private long count;
	private long peak;

	public JVMThreadsMonitorItemTO() {
	}

	public JVMThreadsMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		count = (long) jsonObject.getNumber("count");
		peak = (long) jsonObject.getNumber("peak");
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getPeak() {
		return peak;
	}

	public void setPeak(long peak) {
		this.peak = peak;
	}

}
