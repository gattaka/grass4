package cz.gattserver.grass.monitor.processor.item;

import java.util.ArrayList;
import java.util.List;

import elemental.json.JsonArray;
import elemental.json.JsonObject;
import elemental.json.JsonType;

public class BackupStatusPartItemTO extends MonitorItemTO {

	private List<BackupStatusMonitorItemTO> items = new ArrayList<>();
	private String value;

	public BackupStatusPartItemTO() {
	}

	public BackupStatusPartItemTO(JsonObject jsonObject) {
		super(jsonObject);
		value = jsonObject.getString("value");
		if (JsonType.NULL == jsonObject.get("items").getType())
			return;
		JsonArray array = jsonObject.getArray("items");
		for (int i = 0; i < array.length(); i++)
			items.add(new BackupStatusMonitorItemTO(array.getObject(i)));
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<BackupStatusMonitorItemTO> getItems() {
		return items;
	}

}
