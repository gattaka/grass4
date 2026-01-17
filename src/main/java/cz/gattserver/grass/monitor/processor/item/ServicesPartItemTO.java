package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class ServicesPartItemTO extends ListPartItemTO<ServicesMonitorItemTO> {

	public ServicesPartItemTO() {
	}

	public ServicesPartItemTO(JsonNode jsonObject) {
		super(jsonObject);
	}

	@Override
	protected ServicesMonitorItemTO createItem(JsonNode jsonObject) {
		return new ServicesMonitorItemTO(jsonObject);
	}
}