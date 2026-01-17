package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

public class SystemMemoryMonitorItemTO extends MonitorItemTO {

    private long total;
    private long used;
    private long free;
    private long shared;
    private long buffCache;
    private long available;

    public SystemMemoryMonitorItemTO() {
    }

    public SystemMemoryMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        if (monitorState != MonitorState.SUCCESS) return;
        total = jsonObject.get("total").asLong();
        used = jsonObject.get("used").asLong();
        free = jsonObject.get("free").asLong();
        shared = jsonObject.get("shared").asLong();
        buffCache = jsonObject.get("buffCache").asLong();
        available = jsonObject.get("available").asLong();
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getUsed() {
        return used;
    }

    public void setUsed(long used) {
        this.used = used;
    }

    public long getFree() {
        return free;
    }

    public void setFree(long free) {
        this.free = free;
    }

    public long getShared() {
        return shared;
    }

    public void setShared(long shared) {
        this.shared = shared;
    }

    public long getBuffCache() {
        return buffCache;
    }

    public void setBuffCache(long buffCache) {
        this.buffCache = buffCache;
    }

    public long getAvailable() {
        return available;
    }

    public void setAvailable(long available) {
        this.available = available;
    }

    public float getUsedRation() {
        return (float) used / total;
    }

}
