package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class URLMonitorItemTO extends MonitorItemTO {

	private String name;
	private String url;

	public URLMonitorItemTO(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public URLMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		name = jsonObject.getString("name");
		url = jsonObject.getString("url");
		monitorState = MonitorState.valueOf(jsonObject.getString("monitorState"));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
