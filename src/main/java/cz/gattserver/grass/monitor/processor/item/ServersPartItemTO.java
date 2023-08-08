package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class ServersPartItemTO extends ListPartItemTO<URLMonitorItemTO> {

	public ServersPartItemTO() {
	}

	public ServersPartItemTO(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	protected URLMonitorItemTO createItem(JsonObject jsonObject) {
		return new URLMonitorItemTO(jsonObject);
	}

}
