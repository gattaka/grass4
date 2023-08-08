package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;
import elemental.json.JsonType;

/**
 * TO popisující stav monitorovaného předmětu
 * 
 * @author Hynek
 *
 */
public abstract class MonitorItemTO {

	protected MonitorState monitorState = MonitorState.ERROR;
	protected String stateDetails;
	protected String type;

	public MonitorItemTO() {
		type = this.getClass().getName();
	}

	public MonitorItemTO(JsonObject jsonObject) {
		this();
		monitorState = MonitorState.valueOf(jsonObject.getString("monitorState"));
		if (JsonType.NULL == jsonObject.get("stateDetails").getType())
			return;
		stateDetails = jsonObject.getString("stateDetails");
	}

	/**
	 * Získá stav monitorování
	 */
	public MonitorState getMonitorState() {
		return monitorState;
	}

	public MonitorItemTO setMonitorState(MonitorState monitorState) {
		this.monitorState = monitorState;
		return this;
	}

	public String getStateDetails() {
		return stateDetails;
	}

	public MonitorItemTO setStateDetails(String stateDetails) {
		this.stateDetails = stateDetails;
		return this;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSuccess() {
		return MonitorState.SUCCESS == monitorState;
	}
}
