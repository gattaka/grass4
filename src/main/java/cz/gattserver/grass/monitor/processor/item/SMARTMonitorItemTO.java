package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class SMARTMonitorItemTO extends MonitorItemTO {

    private String time;

    public SMARTMonitorItemTO() {
    }

    public SMARTMonitorItemTO(String time, String stateDetails) {
        this.time = time;
        this.stateDetails = stateDetails;
    }

    public SMARTMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        if (monitorState == MonitorState.ERROR) return;
        time = jsonObject.get("time").asText();
        stateDetails = jsonObject.get("stateDetails").asText();
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}