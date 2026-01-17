package cz.gattserver.grass.monitor.processor.item;

import tools.jackson.databind.JsonNode;

public class URLMonitorItemTO extends MonitorItemTO {

    private String name;
    private String url;

    public URLMonitorItemTO(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public URLMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        name = jsonObject.get("name").asText();
        url = jsonObject.get("url").asText();
        monitorState = MonitorState.valueOf(jsonObject.get("monitorState").asText());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
