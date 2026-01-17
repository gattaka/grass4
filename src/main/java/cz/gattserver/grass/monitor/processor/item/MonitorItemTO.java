package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

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

	public MonitorItemTO(JsonNode jsonObject) {
		this();
		monitorState = MonitorState.valueOf(jsonObject.get("monitorState").asText());
		if (jsonObject.get("stateDetails").isNull())
			return;
		stateDetails = jsonObject.get("stateDetails").asText();
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
