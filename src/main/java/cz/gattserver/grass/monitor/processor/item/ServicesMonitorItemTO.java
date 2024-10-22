package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class ServicesMonitorItemTO extends MonitorItemTO {

	private String unit;
	private String load;
	private String active;
	private String sub;

	public ServicesMonitorItemTO() {
	}

	public ServicesMonitorItemTO(String unit, String load, String active, String sub, String stateDetails) {
		super();
		this.unit = unit;
		this.load = load;
		this.active = active;
		this.sub = sub;
		this.stateDetails = stateDetails;
	}

	public ServicesMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		stateDetails = jsonObject.getString("stateDetails");
		unit = jsonObject.getString("unit");
		load = jsonObject.getString("load");
		active = jsonObject.getString("active");
		sub = jsonObject.getString("sub");
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLoad() {
		return load;
	}

	public void setLoad(String load) {
		this.load = load;
	}

	public String getActive() {
		return active;
	}

	public void setActive(String active) {
		this.active = active;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

}