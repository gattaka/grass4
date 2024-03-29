package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class SystemUptimeMonitorItemTO extends MonitorItemTO {

	private String value;

	public SystemUptimeMonitorItemTO() {
	}

	public SystemUptimeMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		if (monitorState != MonitorState.SUCCESS)
			return;
		stateDetails = jsonObject.getString("stateDetails");
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
