package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class JVMPIDMonitorItemTO extends MonitorItemTO {

	private String pid;

	public JVMPIDMonitorItemTO() {
	}

	public JVMPIDMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		pid = jsonObject.getString("pid");
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
