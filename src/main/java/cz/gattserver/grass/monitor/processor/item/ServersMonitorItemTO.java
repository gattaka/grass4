package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class ServersMonitorItemTO extends MonitorItemTO {

	private String name;
	private String address;
	private int responseCode;

	public ServersMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		name = jsonObject.getString("name");
		address = jsonObject.getString("address");
		responseCode = (int) jsonObject.getNumber("responseCode");
	}

	public ServersMonitorItemTO(String name, String address) {
		this.name = name;
		this.address = address;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

}
