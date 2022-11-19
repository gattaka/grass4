package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class DiskStatusPartItemTO extends ListPartItemTO<DiskStatusMonitorItemTO> {

	public DiskStatusPartItemTO() {
	}
	
	public DiskStatusPartItemTO(JsonObject value) {
		super(value);
	}

	@Override
	protected DiskStatusMonitorItemTO createItem(JsonObject jsonObject) {
		return new DiskStatusMonitorItemTO(jsonObject);
	}

}
