package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class DiskStatusPartItemTO extends ListPartItemTO<DiskStatusMonitorItemTO> {

    public DiskStatusPartItemTO() {
    }

    public DiskStatusPartItemTO(JsonNode value) {
        super(value);
    }

    @Override
    protected DiskStatusMonitorItemTO createItem(JsonNode jsonObject) {
        return new DiskStatusMonitorItemTO(jsonObject);
    }

}
