package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

public class JVMMemoryMonitorItemTO extends MonitorItemTO {

    private long usedMemory;
    private long freeMemory;
    private long maxMemory;
    private long totalMemory;

    public JVMMemoryMonitorItemTO() {
    }

    public JVMMemoryMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        usedMemory = jsonObject.get("usedMemory").asLong();
        freeMemory = jsonObject.get("freeMemory").asLong();
        maxMemory = jsonObject.get("maxMemory").asLong();
        totalMemory = jsonObject.get("totalMemory").asLong();
    }

    public long getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public long getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return maxMemory;
    }

    public void setMaxMemory(long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getTotalMemory() {
        return totalMemory;
    }

    public void setTotalMemory(long totalMemory) {
        this.totalMemory = totalMemory;
    }

}
