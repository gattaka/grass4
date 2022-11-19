package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class SMARTPartItemTO extends ListPartItemTO<SMARTMonitorItemTO> {

	public SMARTPartItemTO() {
	}

	public SMARTPartItemTO(JsonObject jsonObject) {
		super(jsonObject);
	}

	@Override
	protected SMARTMonitorItemTO createItem(JsonObject jsonObject) {
		return new SMARTMonitorItemTO(jsonObject);
	}

}
