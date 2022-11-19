package cz.gattserver.grass.monitor.processor.item;

import java.time.LocalDateTime;

import elemental.json.JsonObject;

public class BackupStatusMonitorItemTO extends MonitorItemTO {

	private String value;
	private LocalDateTime lastTime;

	public BackupStatusMonitorItemTO() {
	}

	public BackupStatusMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		value = jsonObject.getString("value");
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
