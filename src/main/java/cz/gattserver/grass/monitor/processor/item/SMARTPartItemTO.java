package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class SMARTPartItemTO extends ListPartItemTO<SMARTMonitorItemTO> {

    public SMARTPartItemTO() {
    }

    public SMARTPartItemTO(JsonNode jsonObject) {
        super(jsonObject);
    }

    @Override
    protected SMARTMonitorItemTO createItem(JsonNode jsonObject) {
        return new SMARTMonitorItemTO(jsonObject);
    }

}