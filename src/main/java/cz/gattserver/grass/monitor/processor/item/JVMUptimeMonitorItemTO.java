package cz.gattserver.grass.monitor.processor.item;


import tools.jackson.databind.JsonNode;

public class JVMUptimeMonitorItemTO extends MonitorItemTO {

    private long elapsedDays;
    private long elapsedHours;
    private long elapsedMinutes;
    private long elapsedSeconds;

    public JVMUptimeMonitorItemTO() {
    }

    public JVMUptimeMonitorItemTO(JsonNode jsonObject) {
        super(jsonObject);
        elapsedDays = jsonObject.get("elapsedDays").asLong();
        elapsedHours = jsonObject.get("elapsedHours").asLong();
        elapsedMinutes = jsonObject.get("elapsedMinutes").asLong();
        elapsedSeconds = jsonObject.get("elapsedSeconds").asLong();
    }

    public long getElapsedDays() {
        return elapsedDays;
    }

    public long getElapsedHours() {
        return elapsedHours;
    }

    public long getElapsedMinutes() {
        return elapsedMinutes;
    }

    public long getElapsedSeconds() {
        return elapsedSeconds;
    }

    public void setUptime(long uptime) {
        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        elapsedDays = uptime / daysInMilli;
        uptime = uptime % daysInMilli;
        elapsedHours = uptime / hoursInMilli;
        uptime = uptime % hoursInMilli;
        elapsedMinutes = uptime / minutesInMilli;
        uptime = uptime % minutesInMilli;
        elapsedSeconds = uptime / secondsInMilli;
    }

}
