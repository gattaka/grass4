package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

public abstract class ListPartItemTO<T extends MonitorItemTO> extends MonitorItemTO {

    private List<T> items = new ArrayList<T>();

    protected abstract T createItem(JsonNode jsonObject);

    public ListPartItemTO() {
    }

    public ListPartItemTO(JsonNode jsonObject) {
        super(jsonObject);
        if (jsonObject == null) return;
        JsonNode array = jsonObject.get("items");
        for (JsonNode node : array)
            items.add(createItem(node));
    }

    public List<T> getItems() {
        return items;
    }

}