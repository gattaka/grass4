package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

public class JVMThreadsMonitorItemTO extends MonitorItemTO {

    private long count;
    private long peak;

    public JVMThreadsMonitorItemTO() {
    }

    public JVMThreadsMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        count = jsonObject.get("count").asLong();
        peak = jsonObject.get("peak").asLong();
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getPeak() {
        return peak;
    }

    public void setPeak(long peak) {
        this.peak = peak;
    }

}
