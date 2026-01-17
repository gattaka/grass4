package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;


public class BackupStatusPartItemTO extends MonitorItemTO {

    private List<BackupStatusMonitorItemTO> items = new ArrayList<>();

    public BackupStatusPartItemTO() {
    }

    public BackupStatusPartItemTO(JsonNode jsonObject) {
        super(jsonObject);
        stateDetails = jsonObject.get("stateDetails").asText();
        if (jsonObject.get("items").isNull()) return;
        JsonNode array = jsonObject.get("items");
        for (JsonNode node : array)
            items.add(new BackupStatusMonitorItemTO(node));
    }

    public List<BackupStatusMonitorItemTO> getItems() {
        return items;
    }

}