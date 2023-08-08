package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class SMARTMonitorItemTO extends MonitorItemTO {

	private String time;

	public SMARTMonitorItemTO() {
	}

	public SMARTMonitorItemTO(String time, String stateDetails) {
		this.time = time;
		this.stateDetails = stateDetails;
	}

	public SMARTMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		if (monitorState == MonitorState.ERROR)
			return;
		time = jsonObject.getString("time");
		stateDetails = jsonObject.getString("stateDetails");
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}