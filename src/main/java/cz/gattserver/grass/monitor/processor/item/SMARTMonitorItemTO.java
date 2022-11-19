package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class SMARTMonitorItemTO extends MonitorItemTO {

	private String time;
	private String message;

	public SMARTMonitorItemTO() {
	}

	public SMARTMonitorItemTO(String time, String message) {
		this.time = time;
		this.message = message;
	}

	public SMARTMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		if (monitorState == MonitorState.UNAVAILABLE)
			return;
		time = jsonObject.getString("time");
		message = jsonObject.getString("message");
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
