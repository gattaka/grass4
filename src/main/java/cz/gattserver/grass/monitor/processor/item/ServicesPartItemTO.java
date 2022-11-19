package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class ServicesPartItemTO extends ListPartItemTO<ServicesMonitorItemTO> {

	public ServicesPartItemTO() {
	}

	public ServicesPartItemTO(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	protected ServicesMonitorItemTO createItem(JsonObject jsonObject) {
		return new ServicesMonitorItemTO(jsonObject);
	}

}
