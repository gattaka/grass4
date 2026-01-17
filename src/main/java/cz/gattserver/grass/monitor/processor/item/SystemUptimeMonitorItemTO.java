package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

public class SystemUptimeMonitorItemTO extends MonitorItemTO {

	private String value;

	public SystemUptimeMonitorItemTO() {
	}

	public SystemUptimeMonitorItemTO(JsonNode jsonObject) {
		super(jsonObject);
		if (monitorState != MonitorState.SUCCESS)
			return;
		stateDetails = jsonObject.get("stateDetails").asText();
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
