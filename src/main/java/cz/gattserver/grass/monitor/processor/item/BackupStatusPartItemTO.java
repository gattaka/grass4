package cz.gattserver.grass.monitor.processor.item;

import java.util.ArrayList;
import java.util.List;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonType;

public class BackupStatusPartItemTO extends MonitorItemTO {

	private List<BackupStatusMonitorItemTO> items = new ArrayList<>();

	public BackupStatusPartItemTO() {
	}

	public BackupStatusPartItemTO(JsonObject jsonObject) {
		super(jsonObject);
		stateDetails = jsonObject.getString("stateDetails");
		if (JsonType.NULL == jsonObject.get("items").getType())
			return;
		JsonArray array = jsonObject.getArray("items");
		for (int i = 0; i < array.length(); i++)
			items.add(new BackupStatusMonitorItemTO(array.getObject(i)));
	}

	public List<BackupStatusMonitorItemTO> getItems() {
		return items;
	}

}