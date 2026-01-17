package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class ServersPartItemTO extends ListPartItemTO<URLMonitorItemTO> {

	public ServersPartItemTO() {
	}

	public ServersPartItemTO(JsonNode jsonObject) {
		super(jsonObject);
	}

	@Override
	protected URLMonitorItemTO createItem(JsonNode jsonObject) {
		return new URLMonitorItemTO(jsonObject);
	}

}
