package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class ServicesMonitorItemTO extends MonitorItemTO {

    private String unit;
    private String load;
    private String active;
    private String sub;

    public ServicesMonitorItemTO() {
    }

    public ServicesMonitorItemTO(String unit, String load, String active, String sub, String stateDetails) {
        super();
        this.unit = unit;
        this.load = load;
        this.active = active;
        this.sub = sub;
        this.stateDetails = stateDetails;
    }

    public ServicesMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        stateDetails = jsonObject.get("stateDetails").asText();
        unit = jsonObject.get("unit").asText();
        load = jsonObject.get("load").asText();
        active = jsonObject.get("active").asText();
        sub = jsonObject.get("sub").asText();
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

}