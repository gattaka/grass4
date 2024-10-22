package cz.gattserver.grass.monitor.processor.item;

import elemental.json.JsonObject;

public class DiskStatusMonitorItemTO extends MonitorItemTO {

	private String name;
	private long total;
	private long usable;
	private String mount;

	public DiskStatusMonitorItemTO() {
	}

	public DiskStatusMonitorItemTO(JsonObject jsonObject) {
		super(jsonObject);
		if (monitorState != MonitorState.SUCCESS)
			return;
		name = jsonObject.getString("name");
		mount = jsonObject.getString("mount");
		total = (long) jsonObject.getNumber("total");
		usable = (long) jsonObject.getNumber("usable");
	}

	public String getMount() {
		return mount;
	}

	public void setMount(String mount) {
		this.mount = mount;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public long getUsable() {
		return usable;
	}

	public void setUsable(long usable) {
		this.usable = usable;
	}

	public long getUsed() {
		return total - usable;
	}

	public float getUsedRation() {
		return (float) getUsed() / total;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
