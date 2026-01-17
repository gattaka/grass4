package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

import java.time.LocalDateTime;


public class BackupStatusMonitorItemTO extends MonitorItemTO {

	private String value;
	private LocalDateTime lastTime;

	public BackupStatusMonitorItemTO() {
	}

	public BackupStatusMonitorItemTO(JsonNode jsonObject) {
		super(jsonObject);
		value = jsonObject.get("value").textValue();
		// TODO date
	}

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
