package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class ServicesMonitorItemTO extends MonitorItemTO {

	private String unit;
	private String load;
	private String active;
	private String sub;
	private String description;

	public ServicesMonitorItemTO() {
	}

	public ServicesMonitorItemTO(String unit, String load, String active, String sub, String description) {
		super();
		this.unit = unit;
		this.load = load;
		this.active = active;
		this.sub = sub;
		this.description = description;
	}

	public ServicesMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		if (monitorState == MonitorState.UNAVAILABLE)
			return;
		description = jsonObject.getString("description");
		// detaily se zde vyplňují pouze, pokud jde o chybu
		if (monitorState == MonitorState.SUCCESS)
			return;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
