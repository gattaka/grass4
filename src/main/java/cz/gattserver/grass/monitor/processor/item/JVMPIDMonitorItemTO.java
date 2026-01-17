package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

public class JVMPIDMonitorItemTO extends MonitorItemTO {

	private String pid;

	public JVMPIDMonitorItemTO() {
	}

	public JVMPIDMonitorItemTO(JsonNode jsonObject) {
		super(jsonObject);
		pid = jsonObject.get("pid").asText();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

}
